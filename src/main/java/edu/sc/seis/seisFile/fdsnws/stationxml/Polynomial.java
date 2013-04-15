package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Polynomial extends BaseFilterType {

    public Polynomial(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        super.parseAttributes(startE);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // handle buy super
                } else if (elName.equals(StationXMLTagNames.APPROXIMATIONTYPE)) {
                    approximationType = StaxUtil.pullText(reader, StationXMLTagNames.APPROXIMATIONTYPE);
                } else if (elName.equals(StationXMLTagNames.FREQLOWERBOUND)) {
                    freqLowerBound = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQLOWERBOUND);
                } else if (elName.equals(StationXMLTagNames.FREQUPPERBOUND)) {
                    freqUpperBound = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUPPERBOUND);
                } else if (elName.equals(StationXMLTagNames.APPROXLOWERBOUND)) {
                    approxLowerBound = StaxUtil.pullFloat(reader, StationXMLTagNames.APPROXLOWERBOUND);
                } else if (elName.equals(StationXMLTagNames.APPROXUPPERBOUND)) {
                    approxUpperBound = StaxUtil.pullFloat(reader, StationXMLTagNames.APPROXUPPERBOUND);
                } else if (elName.equals(StationXMLTagNames.MAXERROR)) {
                    maxError = StaxUtil.pullFloat(reader, StationXMLTagNames.MAXERROR);
                } else if (elName.equals(StationXMLTagNames.COEFFICIENT)) {
                    coefficientList.add( new FloatNoUnitType(reader, StationXMLTagNames.COEFFICIENT));
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
    
    
    public String getApproximationType() {
        return approximationType;
    }
    
    public Float getFreqLowerBound() {
        return freqLowerBound;
    }
    
    public float getFreqUpperBound() {
        return freqUpperBound;
    }
    
    public float getApproxLowerBound() {
        return approxLowerBound;
    }
    
    public float getApproxUpperBound() {
        return approxUpperBound;
    }
    
    public float getMaxError() {
        return maxError;
    }
    
    public List<FloatNoUnitType> getCoefficientList() {
        return coefficientList;
    }

    private String approximationType;
    private Float freqLowerBound;
    private float freqUpperBound;
    private float approxLowerBound;
    private float approxUpperBound;
    private float maxError;
    private List<FloatNoUnitType> coefficientList = new ArrayList<FloatNoUnitType>();
}
