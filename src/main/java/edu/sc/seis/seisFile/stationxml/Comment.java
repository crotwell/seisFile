package edu.sc.seis.seisFile.stationxml;

import java.util.Date;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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

    public Person getAuthor() {
        return author;
    }

    String value;

    String beginEffectiveTime, endEffectiveTime;

    int id;

    Person author;

    public String toString() {
        return value;
    }
}
