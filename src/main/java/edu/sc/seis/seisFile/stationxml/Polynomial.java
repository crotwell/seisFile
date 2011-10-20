package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Polynomial extends AbstractResponseType {

    public Polynomial(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.APPROXIMATIONTYPE)) {
                    approximationType = StaxUtil.pullText(reader, StationXMLTagNames.APPROXIMATIONTYPE);
                } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
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
                    coefficientList.add( StaxUtil.pullFloat(reader, StationXMLTagNames.COEFFICIENT));
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
    
    public List<Float> getCoefficientList() {
        return coefficientList;
    }

    private String approximationType;
    private Float freqLowerBound;
    private float freqUpperBound;
    private float approxLowerBound;
    private float approxUpperBound;
    private float maxError;
    private List<Float> coefficientList = new ArrayList<Float>();
}
