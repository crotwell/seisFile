package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.SAXException;

import edu.sc.seis.seisFile.BuildVersion;


public abstract class AbstractFDSNQuerier implements AutoCloseable {

    public AbstractFDSNQuerier() {}

    public abstract URI formURI() throws URISyntaxException;

    public abstract URL getSchemaURL();

    public void connect() throws URISyntaxException, FDSNWSException {
        connectionUri = formURI();
        try {
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                    .setConnectTimeout(getConnectTimeout())
                    .setConnectionRequestTimeout(getConnectTimeout())
                    .setSocketTimeout(getReadTimeout())
                    .setRedirectsEnabled(true);
            if (getProxyHost() != null) {
              HttpHost proxy = new HttpHost(getProxyHost(), getProxyPort(), getProxyProtocol());
              requestConfigBuilder.setProxy(proxy);
            }
            RequestConfig requestConfig = requestConfigBuilder.build();
            CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpGet request = new HttpGet(connectionUri);
            request.setHeader("User-Agent", getUserAgent());
            request.setHeader("Accept", getAcceptHeader());
            TimeQueryLog.add(connectionUri);
            response = httpClient.execute(request);
            processConnection(response);
        } catch(IOException e) {
            throw new FDSNWSException("Problem with connection", e, connectionUri);
        } catch(RuntimeException e) {
            throw new FDSNWSException("Problem with connection", e, connectionUri);
        }
    }

    protected void processConnection(CloseableHttpResponse response) throws IOException, FDSNWSException {
        responseCode = response.getStatusLine().getStatusCode();
        if (responseCode == 204) {
            empty = true;
            response.close();
            response = null;
            return;
        } else if (responseCode != 200) {
            error = true;
            errorMessage = "Code: " + responseCode + " " + extractErrorMessage(response);
            response.close();
            response = null;
            throw new FDSNWSException(errorMessage, connectionUri, responseCode);
        }
        HttpEntity entity = response.getEntity();
        inputStream = new BufferedInputStream(entity.getContent(), 64*1024);
    }

    public static void validate(XMLStreamReader reader, URL schemaURL) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(schemaURL);
        Validator validator = schema.newValidator();
        // StringWriter buf = new StringWriter();
        // Result result = new StaXResult(buf);
        validator.validate(new StAXSource(reader), null);
    }

    public void outputRaw(OutputStream out) throws MalformedURLException, IOException, URISyntaxException,
            FDSNWSException {
        connect();
        outputRaw(getInputStream(), out);
    }

    public void outputRaw(InputStream in, OutputStream out) throws IOException {
        BufferedInputStream bufIn = new BufferedInputStream(in);
        BufferedOutputStream bufOut = new BufferedOutputStream(out);
        byte[] buf = new byte[1024];
        int numRead = bufIn.read(buf);
        while (numRead != -1) {
            bufOut.write(buf, 0, numRead);
            numRead = bufIn.read(buf);
        }
        bufIn.close(); // close as we hit EOF
        bufOut.flush();// only flush in case outside wants to write more
    }

    public String getRawXML() throws IOException {
        StringWriter out = new StringWriter();
        checkConnectionInitiated();
        BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
        char[] buf = new char[1024];
        int numRead = in.read(buf);
        while (numRead != -1) {
            out.write(buf, 0, numRead);
            numRead = in.read(buf);
        }
        in.close();
        out.close();
        return out.toString();
    }

    public boolean isError() {
        checkConnectionInitiated();
        return error;
    }

    public String getErrorMessage() {
        checkConnectionInitiated();
        return errorMessage;
    }

    public boolean isEmpty() {
        checkConnectionInitiated();
        return empty;
    }

    public InputStream getInputStream() {
        checkConnectionInitiated();
        return inputStream;
    }

    /**
     * returns the URI that was used to open the connection. This may be null if
     * connect() has not yet been called. formUri() can be used to get the URI
     * without connecting.
     *
     * @return
     */
    public URI getConnectionUri() {
        return connectionUri;
    }

    public boolean isConnectionInitiated() {
        return response != null;
    }

    public void checkConnectionInitiated() {
        if (!isConnectionInitiated()) {
            throw new RuntimeException("Not connected yet");
        }
    }

    public XMLEventReader getReader() throws XMLStreamException, URISyntaxException {
        if (reader == null && inputStream != null) {
            XMLInputFactory2 factory = (XMLInputFactory2)XMLInputFactory.newInstance();
            XMLStreamReader2 sr = (XMLStreamReader2)factory.createXMLStreamReader(getConnectionUri().toString(),
                                                                                  getInputStream());
            if (isValidate()) {
                try {
                    XMLValidationSchema schema = schemaCache.get(getSchemaURL());
                    if (schema == null) {
                        if (sfactory == null) {
                            sfactory = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
                        }
                        schema = sfactory.createSchema(getSchemaURL());
                        schemaCache.put(getSchemaURL(), schema);
                    }
                    sr.validateAgainst(schema);
                } catch(XMLStreamException e) {
                    throw new RuntimeException("should not happen, can't load schema in jar: " + getSchemaURL(), e);
                }
            }
            reader = (XMLEventReader2)factory.createXMLEventReader(sr);
        }
        return reader;
    }

    public static String extractErrorMessage(HttpResponse response) {
        String out = "";
        BufferedReader errReader = null;
        try {
            HttpEntity entity = response.getEntity();
            InputStream inError = entity.getContent();
            if (inError == null) {
                out = "<Empty Error Message From Server>";
            } else {
                if ("gzip".equals(entity.getContentEncoding())) {
                    inError = new GZIPInputStream(inError);
                }
                int maxLines = 1000;
                int lineNum = 0;
                errReader = new BufferedReader(new InputStreamReader(inError));
                for (String line; (line = errReader.readLine()) != null && lineNum < maxLines;) {
                    out += line + "\n";
                    lineNum++;
                }
                if (lineNum == maxLines) {
                    out += "...output truncated at " + maxLines + " lines.";
                }
            }
        } catch(IOException e) {
            out += "\nException reading error stream: " + e.toString();
        } finally {
            if (errReader != null)
                try {
                    errReader.close();
                } catch(IOException e) {
                    // oh well
                }
        }
        return out;
    }


    public String getAcceptHeader() {
        return acceptHeader;
    }

    /** set the Accept: html header, default is application/xml.
     *
     */
    public void setAcceptHeader(String acceptHeader) {
        this.acceptHeader = acceptHeader;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getProxyPort() {
        if (proxyPort <= 0) {
          return 80;
        }
        return proxyPort;
    }

    public void setProxyProtocol(String proxyProtocol) {
        this.proxyProtocol = proxyProtocol;
    }

    public String getProxyProtocol() {
        if (proxyProtocol == null) {
          return "http";
        }
        return proxyProtocol;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public HttpResponse getHttpResponse() {
        return response;
    }

    /** set the HttpConnection connectionTimeout in milliseconds. */
    public void setConnectTimeout(int milliseconds) {
        connectTimeout = milliseconds;
    }

    /** set the HttpConnection readTimeout in milliseconds. */
    public void setReadTimeout(int milliseconds) {
        readTimeout = milliseconds;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static Throwable extractRootCause(Throwable t) {
        if (t.getCause() == null) {
            return t;
        }
        return extractRootCause(t.getCause());
    }

    /**
     * trys to grab any text after the error point that is already buffered to
     * help see xml errors. Wraps the XMLStreamException in a
     * {@link FDSNWSException} and rethrows it.
     * */
    void handleXmlStreamException(XMLStreamException e) throws FDSNWSException {
        String bufferedText = "";
        try {
            int avail = getInputStream().available();
            if (avail > 0) {
                byte[] b = new byte[avail];
                int numRead = getInputStream().read(b);
                bufferedText = new String(b, 0, numRead);
            }
        } catch(IOException ee) {}
        throw new FDSNWSException("Unable to load xml, text past error='" + bufferedText + "'", e, getConnectionUri());
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch(XMLStreamException e) {
                logger.warn("can't close reader", e);
            }
            reader = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch(org.apache.http.MalformedChunkCodingException ee) {
            	//not sure, but don't think we care about this...
            } catch(IOException e) {
                logger.warn("can't close inputstream: "+connectionUri, e);
            } finally {
            	inputStream = null;
            }
            inputStream = null;
        }
        if (response != null) {
            try {
                response.close();
            } catch(javax.net.ssl.SSLException e) {
                // oh well
            } catch(IOException e) {
                logger.warn("can't close response", e);
            } finally {
            	response = null;
            }
            response = null;
        }
    }

    static XMLValidationSchemaFactory sfactory;

    static HashMap<URL, XMLValidationSchema> schemaCache = new HashMap<URL, XMLValidationSchema>();
    
    public static final String DEFAULT_USER_AGENT = "SeisFile-"+BuildVersion.getVersion();
    
    String userAgent = DEFAULT_USER_AGENT;

    String acceptHeader = "application/xml";

    int responseCode;

    boolean error;

    String errorMessage;

    boolean empty;

    XMLEventReader reader;

    InputStream inputStream;

    CloseableHttpResponse response;

    protected String proxyHost = null;

    protected int proxyPort = -1;

    protected String proxyProtocol = null;

    // URLConnection urlConn;
    protected URI connectionUri;

    protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    protected int readTimeout = DEFAULT_READ_TIMEOUT;

    protected boolean validate = false;

    public static int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;

    public static int DEFAULT_READ_TIMEOUT = 60 * 1000;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractFDSNQuerier.class);
}
