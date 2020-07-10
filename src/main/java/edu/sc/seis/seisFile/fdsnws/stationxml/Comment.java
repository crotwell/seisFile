package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Comment {

    public Comment(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (parseSubElement(elName, reader)) {
                    // super handled it
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

    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.VALUE)) {
            value = StaxUtil.pullText(reader, StationXMLTagNames.VALUE);
            return true;
        } else if (elName.equals(StationXMLTagNames.BEGINEFFECTIVETIME)) {
            beginEffectiveTime = StaxUtil.pullText(reader, StationXMLTagNames.BEGINEFFECTIVETIME);
            return true;
        } else if (elName.equals(StationXMLTagNames.ENDEFFECTIVETIME)) {
            endEffectiveTime = StaxUtil.pullText(reader, StationXMLTagNames.ENDEFFECTIVETIME);
            return true;
        } else if (elName.equals(StationXMLTagNames.AUTHOR)) {
            authorList.add( new Person(reader, StationXMLTagNames.AUTHOR));
            return true;
        } else {
            return false;
        }
    }

    void parseAttributes(StartElement startE) throws StationXMLException {
        id = StaxUtil.pullIntAttribute(startE, StationXMLTagNames.ID);
    }

    public String getValue() {
        return value;
    }

    public String getBeginEffectiveTime() {
        return beginEffectiveTime;
    }

    public String getEndEffectiveTime() {
        return endEffectiveTime;
    }

    public int getId() {
        return id;
    }

    /**
     *  @deprecated
     *  use getAuthorList() as can have more than one author
     */
    @Deprecated
    public Person getAuthor() {
        if (authorList.size() > 0) {
            return authorList.get(0);
        }
        return null;
    }

    public List<Person> getAuthorList() {
        return authorList;
    }

    String value;

    String beginEffectiveTime, endEffectiveTime;

    int id;

    List<Person> authorList = new ArrayList<Person>();

    public String toString() {
        return value;
    }
}
