package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

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
}
