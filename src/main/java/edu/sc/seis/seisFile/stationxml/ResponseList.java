package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class ResponseList extends BaseFilterType {
    
    public ResponseList(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        super.parseAttributes(startE);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // handle buy super
                } else if (elName.equals(StationXMLTagNames.RESPONSELISTELEMENT)) {
                    responseElements.add( new ResponseListElement(reader));
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else  {
                e = reader.nextEvent();
            }
        }
    }
    
    
    public List<ResponseListElement> getResponseElements() {
        return responseElements;
    }

    private List<ResponseListElement> responseElements = new ArrayList<ResponseListElement>();
}
