package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class FIR extends AbstractResponseType {

    
    public FIR(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.RESPONSENAME)) {
                    responseName = StaxUtil.pullText(reader, StationXMLTagNames.RESPONSENAME);
                } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.SYMMETRY)) {
                    symmetry = StaxUtil.pullText(reader, StationXMLTagNames.SYMMETRY);
                } else if (elName.equals(StationXMLTagNames.NUMERATORCOEFFICIENT)) {
                    numeratorCoefficientList.add(StaxUtil.pullFloat(reader, StationXMLTagNames.NORMALIZATIONFACTOR));
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

    public String getResponseName() {
        return responseName;
    }
    
    public String getSymmetry() {
        return symmetry;
    }
    
    public List<Float> getNumeratorCoefficientList() {
        return numeratorCoefficientList;
    }
    
    String responseName, symmetry;
    List<Float> numeratorCoefficientList = new ArrayList<Float>();
}
