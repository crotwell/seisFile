package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class IrisCommentList {

    public IrisCommentList(String elementName) {
        this.elementName = elementName;
    }

    public IrisCommentList(XMLEventReader reader, String elementName) throws XMLStreamException, StationXMLException {
        this(elementName);
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.IRISCOMMENT)) {
                    list.add(new IrisComment(reader));
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

    public String getElementName() {
        return elementName;
    }

    public List<IrisComment> getList() {
        return list;
    }

    private String elementName;

    private List<IrisComment> list = new ArrayList<IrisComment>();
}
