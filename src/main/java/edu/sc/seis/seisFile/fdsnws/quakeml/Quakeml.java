package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.http.client.methods.CloseableHttpResponse;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQuerier;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;

public class Quakeml {

    public static Quakeml createEmptyQuakeML() {
        try {
            URL url = Quakeml.class.getClassLoader().getResource("edu/sc/seis/seisFile/quakeml/1.2/empty.quakeml");
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader r;
            r = factory.createXMLEventReader(url.toString(), url.openStream());
            return new Quakeml(r);
        } catch(Exception e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    public static URL findInternalSchema() {
        return FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/quakeml/1.2/QuakeML-1.2.xsd");
    }

    public static URL findInternalBEDSchema() {
        return FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/quakeml/1.2/QuakeML-BED-1.2.xsd");
    }
    
    public static void printSchema(OutputStream out) throws IOException {
        BufferedInputStream bufIn = new BufferedInputStream(findInternalBEDSchema().openStream());
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


    public Quakeml(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        this.reader = reader;
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.QUAKEML, reader);
        schemaVersion = startE.getName().getNamespaceURI();
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.eventParameter)) {
                    eventParameters = new EventParameters(reader);
                    break;
                } else {
                    System.err.println("QuakeML skipping "+elName);
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }

    public boolean checkSchemaVersion() {
        return QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION.equals(schemaVersion);
    }

    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                // oh well
            }
        }
        reader = null;
        if (response != null) {
            try {
                response.close();
            } catch(IOException e) {
                // oh well
            }
        }
        response = null;
    }

    public EventParameters getEventParameters() {
        return eventParameters;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setEventParameters(EventParameters eventParameters) {
        this.eventParameters = eventParameters;
    }

    public void setQuerier(FDSNEventQuerier q) {
        this.querier = q;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    EventParameters eventParameters;
    
    XMLEventReader reader;


    String schemaVersion;
    
    CloseableHttpResponse response;
    /* this is so that the querier will not be garbage collected while the QuakeML is being processed. */
    FDSNEventQuerier querier;
}
