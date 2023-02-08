package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.io.IOException;
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

    public EventParameters getEventParameters() {
        return eventParameters;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    EventParameters eventParameters;

    XMLEventReader reader;

    String schemaVersion;

    public boolean checkSchemaVersion() {
        return QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION.equals(schemaVersion);
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
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
	    } catch(javax.net.ssl.SSLException e) {
		    // oh well
            } catch(IOException e) {
                // oh well
            }
        }
        response = null;
    }

    CloseableHttpResponse response;
    
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


    public static URL loadSchema() {
        return FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/quakeml/1.2/QuakeML-1.2.xsd");
    }
    
    /* this is so that the querier will not be garbage collected while the QuakeML is being processed. */
    FDSNEventQuerier querier;
    public void setQuerier(FDSNEventQuerier q) {
        this.querier = q;
    }
}
