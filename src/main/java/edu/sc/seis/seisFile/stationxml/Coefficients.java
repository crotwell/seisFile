package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Coefficients extends AbstractResponseType {

    public Coefficients(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.COEFFICIENTS, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.NUMERATOR)) {
                    numeratorList.add( StaxUtil.pullFloat(reader, StationXMLTagNames.NUMERATOR));
                } else if (elName.equals(StationXMLTagNames.DENOMINATOR)) {
                    denominatorList.add( StaxUtil.pullFloat(reader, StationXMLTagNames.DENOMINATOR));
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

    
    public List<Float> getNumeratorList() {
        return numeratorList;
    }
    
    public List<Float> getDenominatorList() {
        return denominatorList;
    }

    List<Float> numeratorList = new ArrayList<Float>();
    List<Float> denominatorList = new ArrayList<Float>();
}
