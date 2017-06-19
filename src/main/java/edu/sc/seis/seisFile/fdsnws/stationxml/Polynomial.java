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

    public Polynomial(String resourceId,
                      String name,
                      String description,
                      Unit inputUnits,
                      Unit outputUnits,
                      String approximationType,
                      FloatType freqLowerBound,
                      FloatType freqUpperBound,
                      float approxLowerBound,
                      float approxUpperBound,
                      float maxError,
                      List<FloatNoUnitType> coefficientList) {
        super(resourceId, name, description, inputUnits, outputUnits);
        this.approximationType = approximationType;
        this.freqLowerBound = freqLowerBound;
        this.freqUpperBound = freqUpperBound;
        this.approxLowerBound = approxLowerBound;
        this.approxUpperBound = approxUpperBound;
        this.maxError = maxError;
        this.coefficientList = coefficientList;
    }

    public Polynomial(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        this(reader, StationXMLTagNames.POLYNOMIAL);
    }
    
    public Polynomial(XMLEventReader reader, String elementName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
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
                    freqLowerBound = new FloatType(reader, StationXMLTagNames.FREQLOWERBOUND, "HERTZ");
                } else if (elName.equals(StationXMLTagNames.FREQUPPERBOUND)) {
                    freqUpperBound = new FloatType(reader, StationXMLTagNames.FREQUPPERBOUND, "HERTZ");
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
    
    public FloatType getFreqLowerBound() {
        return freqLowerBound;
    }
    
    public FloatType getFreqUpperBound() {
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
    private FloatType freqLowerBound;
    private FloatType freqUpperBound;
    private float approxLowerBound;
    private float approxUpperBound;
    private float maxError;
    private List<FloatNoUnitType> coefficientList = new ArrayList<FloatNoUnitType>();
}
