package edu.sc.seis.seisFile.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StationXMLException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;


public class EventParameters {
    
    public EventParameters(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        this.reader = reader;
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.eventParameter, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.description)) {
                    description = StaxUtil.pullText(reader, QuakeMLTagNames.description);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.event)) {
                    events = new EventIterator(reader);
                    break;
                } else {
                    System.out.println("In EventParameters Skipping: "+elName);
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
    
    
    public String getDescription() {
        return description;
    }

    
    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    
    public EventIterator getEvents() {
        return events;
    }

    
    public List<Comment> getCommentList() {
        return commentList;
    }

    String description;

    CreationInfo creationInfo;
    
    EventIterator events;
    
    List<Comment> commentList = new ArrayList<Comment>();
    
    XMLEventReader reader;
}
