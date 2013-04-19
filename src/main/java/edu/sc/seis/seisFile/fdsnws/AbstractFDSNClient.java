package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.martiansoftware.jsap.JSAPException;

import edu.sc.seis.seisFile.client.AbstractClient;

public class AbstractFDSNClient extends AbstractClient {

    public AbstractFDSNClient(String[] args) throws JSAPException {
        super(args);
    }

    void connect(URI uri) throws MalformedURLException, IOException, XMLStreamException {
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
                errorMessage = "";
                BufferedReader errReader = null;
                try {
                    errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    for (String line; (line = errReader.readLine()) != null;) {
                        errorMessage += line + "\n";
                    }
                } finally {
                    if (errReader != null)
                        try {
                            errReader.close();
                            conn.disconnect();
                        } catch(IOException e) {
                            throw e;
                        }
                }
                return;
            } else {
                // likely not an error in the http layer, so assume XML is
                // returned
                XMLInputFactory factory = XMLInputFactory.newInstance();
                reader = factory.createXMLEventReader(uri.toString(), urlConn.getInputStream());
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

    public XMLEventReader getReader() {
        return reader;
    }

    boolean error;

    String errorMessage;

    boolean empty;

    XMLEventReader reader;
    

    public static final String BEGIN = "begin";

    public static final String END = "end";
}
