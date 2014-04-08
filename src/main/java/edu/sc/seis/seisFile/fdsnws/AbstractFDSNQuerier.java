package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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

import org.xml.sax.SAXException;

import edu.sc.seis.seisFile.client.AbstractClient;

public abstract class AbstractFDSNQuerier {

    public AbstractFDSNQuerier() {
    }
    
    public abstract URI formURI() throws URISyntaxException;
    
    public void connect() throws URISyntaxException, FDSNWSException {
        connectionUri = formURI();
        try {
            urlConn = connectionUri.toURL().openConnection();
            urlConn.setConnectTimeout(getConnectTimeout());
            urlConn.setReadTimeout(getReadTimeout());
            if (urlConn instanceof HttpURLConnection) {
                ((HttpURLConnection)urlConn).setRequestProperty("User-Agent", getUserAgent());
                ((HttpURLConnection)urlConn).setRequestProperty("Accept", "application/xml");
                ((HttpURLConnection)urlConn).setRequestProperty("Accept-Encoding", "gzip, deflate");
            }
            processConnection(urlConn);
        } catch(IOException e) {
            throw new FDSNWSException("Problem with connection", e, connectionUri);
        } catch(RuntimeException e) {
            throw new FDSNWSException("Problem with connection", e, connectionUri);
        }
    }

    protected void processConnection(URLConnection urlConn) throws IOException {
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection)urlConn;
            responseCode = conn.getResponseCode();
            if (responseCode == 204) {
                empty = true;
                return;
            } else if (responseCode != 200) {
                error = true;
                errorMessage = "Code: "+responseCode+" "+extractErrorMessage(conn);
                return;
            }
        }
        // likely not an error in the http layer, so content is returned
        if ("gzip".equals(urlConn.getContentEncoding())) {
            inputStream = new GZIPInputStream(new BufferedInputStream(urlConn.getInputStream()));
        } else {
            inputStream = new BufferedInputStream(urlConn.getInputStream());
        }
    }

    public static void validate(XMLStreamReader reader, URL schemaURL) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(schemaURL);
        Validator validator = schema.newValidator();
        // StringWriter buf = new StringWriter();
        // Result result = new StaXResult(buf);
        validator.validate(new StAXSource(reader), null);
    }

    public void outputRaw(OutputStream out) throws MalformedURLException, IOException, URISyntaxException, FDSNWSException {
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
        BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
        char[] buf = new char[1024];
        int numRead = in.read(buf);
        while(numRead != -1) {
            out.write(buf, 0, numRead);
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

    /** returns the URI that was used to open the connection.
     * This may be null if connect() has not yet been called. formUri()
     * can be used to get the URI without connecting.
     * @return
     */
    public URI getConnectionUri() {
        return connectionUri;
    }

    public boolean isConnectionInitiated() {
        return urlConn != null;
    }
    
    public void checkConnectionInitiated() {
        if ( ! isConnectionInitiated()) {
            throw new RuntimeException("Not connected yet");
        }
    }

    public XMLEventReader getReader() throws XMLStreamException, URISyntaxException {
        if (reader == null) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLEventReader(getConnectionUri().toString(), getInputStream());
        }
        return reader;
    }

    public static String extractErrorMessage(HttpURLConnection conn) {
        String out = "";
        BufferedReader errReader = null;
        try {
            InputStream inError = conn.getErrorStream();
            if (inError == null) {
                out = "<Empty Error Message From Server>";
            } else {
                if ("gzip".equals(conn.getContentEncoding())) {
                    inError = new GZIPInputStream(inError);
                }
                errReader = new BufferedReader(new InputStreamReader(inError));
                for (String line; (line = errReader.readLine()) != null;) {
                    out += line + "\n";
                }
            }
        } catch(IOException e) {
            out += "\nException reading error stream: " + e.toString();
        } finally {
            if (errReader != null)
                try {
                    errReader.close();
                    conn.disconnect();
                } catch(IOException e) {
                    // oh well
                }
        }
        return out;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }
    
    public int getResponseCode() {
        return responseCode;
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

    String userAgent = AbstractClient.DEFAULT_USER_AGENT;

    int responseCode;
    
    boolean error;

    String errorMessage;

    boolean empty;

    XMLEventReader reader;

    InputStream inputStream;
    
    URLConnection urlConn;

    protected URI connectionUri;

    protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    protected int readTimeout = DEFAULT_READ_TIMEOUT;

    public static int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;

    public static int DEFAULT_READ_TIMEOUT = 60 * 1000;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractFDSNQuerier.class);

}
