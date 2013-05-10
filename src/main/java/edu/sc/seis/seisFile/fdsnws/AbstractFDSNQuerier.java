package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import edu.sc.seis.seisFile.client.AbstractClient;


public abstract class AbstractFDSNQuerier {

    void connect(URI uri) throws MalformedURLException, IOException {
        connectionUri = uri;
        URLConnection urlConn = uri.toURL().openConnection();
        if (urlConn instanceof HttpURLConnection) {
            ((HttpURLConnection)urlConn).setRequestProperty("User-Agent", getUserAgent());
        }
        processConnection(urlConn);
    }
    
    protected void processConnection(URLConnection urlConn) throws MalformedURLException, IOException {
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection)urlConn;
            if (conn.getResponseCode() == 204) {
                empty = true;
                return;
            } else if (conn.getResponseCode() != 200) {
                error = true;
                System.err.println("Response Code :" + conn.getResponseCode());
                errorMessage = extractErrorMessage(conn);
                return;
            }
        }
        // likely not an error in the http layer, so content is returned
        inputStream = urlConn.getInputStream();
    }
    
    protected void validate(XMLStreamReader reader, URL schemaURL) throws SAXException, IOException {
        
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(schemaURL);

        Validator validator = schema.newValidator();
        //StringWriter buf = new StringWriter();
        //Result result = new StaXResult(buf);
        validator.validate(new StAXSource(reader), null);
        
    }
    
    public void outputRaw(InputStream in, OutputStream out) throws IOException {
        BufferedInputStream bufIn = new BufferedInputStream(in);
        BufferedOutputStream bufOut = new BufferedOutputStream(out);
        byte[] buf = new byte[1024];
        int numRead = bufIn.read(buf);
        while(numRead != -1) {
            bufOut.write(buf, 0, numRead);
            numRead = bufIn.read(buf);
        }
        bufIn.close(); // close as we hit EOF
        bufOut.flush();// only flush in case outside wants to write more
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

    public URI getConnectionUri() {
        checkConnectionInitiated();
        return connectionUri;
    }
    
    public void checkConnectionInitiated() {
        if (connectionUri == null) {
            throw new RuntimeException("Not connected yet");
        }
    }

    public XMLEventReader getReader() throws XMLStreamException {
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
            errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            for (String line; (line = errReader.readLine()) != null;) {
                out += line + "\n";
            }
        } catch(IOException e) {
            out += "\nException reading error strea: "+e.toString();
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
    
    String userAgent = AbstractClient.DEFAULT_USER_AGENT;
    
    boolean error;

    String errorMessage;

    boolean empty;
    
    XMLEventReader reader;

    InputStream inputStream;

    protected URI connectionUri;
}
