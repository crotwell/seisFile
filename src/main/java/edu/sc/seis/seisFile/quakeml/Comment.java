package edu.sc.seis.seisFile.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;


public class Comment {
    
    public Comment(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.eventParameter, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.text)) {
                    text = StaxUtil.pullText(reader, QuakeMLTagNames.text);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
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
    
    
    public String getText() {
        return text;
    }

    
    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    String text;

    CreationInfo creationInfo;
}
