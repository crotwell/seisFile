package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;

import edu.sc.seis.seisFile.client.AbstractClient;

public class AbstractFDSNClient extends AbstractClient {

    public AbstractFDSNClient(String[] args) throws JSAPException {
        super(args);
    }

    protected void addParams() throws JSAPException {
        super.addParams();
        add(new Switch(PRINTURL, JSAP.NO_SHORTFLAG, PRINTURL, "Construct and print URL and exit"));
    }

    void connect(URI uri) throws MalformedURLException, IOException {
        connectionUri = uri;
        URLConnection urlConn = uri.toURL().openConnection();
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection)urlConn;
            conn.setRequestProperty("User-Agent", getUserAgent());
            if (conn.getResponseCode() == 204) {
                empty = true;
                return;
            } else if (conn.getResponseCode() != 200) {
                error = true;
                System.err.println("Response Code :" + conn.getResponseCode());
                errorMessage = extractErrorMessage(conn);
                return;
            } else {
                // likely not an error in the http layer, so content is returned
                inputStream = urlConn.getInputStream();
            }
        }
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isEmpty() {
        return empty;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public URI getConnectionUri() {
        return connectionUri;
    }

    public XMLEventReader getReader() throws XMLStreamException {
        if (reader == null) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLEventReader(getConnectionUri().toString(), getInputStream());
        }
        return reader;
    }

    boolean error;

    String errorMessage;

    boolean empty;

    XMLEventReader reader;

    InputStream inputStream;

    private URI connectionUri;

    public static final String PRINTURL = "printurl";

    public static final String BEGIN = "begin";

    public static final String END = "end";

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
}
