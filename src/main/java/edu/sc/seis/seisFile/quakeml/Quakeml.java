package edu.sc.seis.seisFile.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StationXMLException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;


public class Quakeml {
    
    public Quakeml(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        this.reader = reader;
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.QUAKEML, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.eventParameter)) {
                    eventParameters = new EventParameters(reader);
                    break;
                } else {
                    System.out.println("In Quakeml Skipping: "+elName);
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

    EventParameters eventParameters;
    
    XMLEventReader reader;

    public boolean checkSchemaVersion() {
        // TODO Auto-generated method stub
        return false;
    }
}
