package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


public class FIR extends BaseFilterType {

    
    public FIR(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        super.parseAttributes(startE);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // handle buy super
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
