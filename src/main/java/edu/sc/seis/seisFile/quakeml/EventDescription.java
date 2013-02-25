package edu.sc.seis.seisFile.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.stationxml.StationXMLException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;


public class EventDescription {
    
    public EventDescription(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.description, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.text)) {
                    text = StaxUtil.pullText(reader, QuakeMLTagNames.text);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
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
    
    public String toString() {
        return getType()+": "+(getText()==null?"":getText());
    }
    
    public String getText() {
        return text;
    }

    
    public String getType() {
        return type;
    }

    String text, type;
}
