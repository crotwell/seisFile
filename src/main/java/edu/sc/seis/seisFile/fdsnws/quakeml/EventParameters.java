package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class EventParameters {
    
    public EventParameters(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.eventParameter, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.event)) {
                    events = new EventIterator(reader, this);
                    break;
                } else {
                    processOneStartElement(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
    }

    public void setEvents(EventIterator events) {
        this.events = events;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    void processOneStartElement(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        XMLEvent e = reader.peek();
        if (e.isStartElement()) {
            String elName = e.asStartElement().getName().getLocalPart();
            if (elName.equals(QuakeMLTagNames.description)) {
                description = StaxUtil.pullText(reader, QuakeMLTagNames.description);
            } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                creationInfo = new CreationInfo(reader);
            } else if (elName.equals(QuakeMLTagNames.comment)) {
                commentList.add(new Comment(reader));
            } else {
                StaxUtil.skipToMatchingEnd(reader);
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
        if (events == null) {
            events = new EventIterator(null, this) {

                @Override
                public boolean hasNext() throws XMLStreamException {
                    return false;
                }

                @Override
                public Event next() throws XMLStreamException, SeisFileException {
                    throw new SeisFileException("No mo events");
                }
                
            };
        }
        return events;
    }

    
    public List<Comment> getCommentList() {
        return commentList;
    }

    String description;

    CreationInfo creationInfo;
    
    EventIterator events;
    
    List<Comment> commentList = new ArrayList<Comment>();
    
}
