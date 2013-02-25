package edu.sc.seis.seisFile.quakeml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StaMessage;
import edu.sc.seis.seisFile.stationxml.StationXMLTagNames;


public class QuakeMLClient {

    public QuakeMLClient() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws IOException 
     * @throws XMLStreamException 
     * @throws SeisFileException 
     */
    public static void main(String[] args) throws IOException, XMLStreamException, SeisFileException {
        if (args.length != 2 || ! args[0].equals("-u")) {
            System.out.println("Usage: stationxmlclient -u url");
            System.out.println("       stationxmlclient -u http://www.iris.edu/ws/station/query?net=IU&sta=SNZO&chan=BHZ&level=chan");
            return;
        }
        URL url = new URL(args[1]);
        URLConnection urlConn = url.openConnection();
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection)urlConn;
            if (conn.getResponseCode() != 200) {
                String out = "";
                BufferedReader errReader = null;
                try {
                    errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    for (String line; (line = errReader.readLine()) != null;) {
                        out += line + "\n";
                    }
                } finally {
                    if (errReader != null) try { 
                        errReader.close(); 
                        conn.disconnect();
                    } catch (IOException e) {
                        throw e;
                    }
                }
                System.err.println("Error in connection with url: "+url);
                System.err.println(out);
                return;
            }
        }
        
        // likely not an error in the http layer, so assume XML is returned
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(url.toString(), urlConn.getInputStream());
        XMLEvent e = r.peek();
        while (!e.isStartElement()) {
            e = r.nextEvent(); // eat this one
            e = r.peek(); // peek at the next
        }
        System.out.println("StaMessage");
        Quakeml quakeml = new Quakeml(r);
        if ( ! quakeml.checkSchemaVersion()) {
            System.out.println("");
            System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
            System.out.println("XmlSchema (code): "+StationXMLTagNames.SCHEMA_VERSION);
            System.out.println("");
        }
    }
}
