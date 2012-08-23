package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class ResponseList extends AbstractResponseType {
    
    public ResponseList(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
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
