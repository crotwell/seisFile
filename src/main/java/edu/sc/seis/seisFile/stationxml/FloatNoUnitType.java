package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class FloatNoUnitType {
    
    public FloatNoUnitType(String tagName) throws XMLStreamException, StationXMLException {
        this.tagName = tagName;
    }
    
    void parseAttributes(StartElement startE) throws StationXMLException {
        String plusErrorStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.PLUSERROR);
        String minusErrorStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.MINUSERROR);
        Float plusError = null;
        if (plusErrorStr != null) {
            plusError = Float.parseFloat(plusErrorStr);
        }
        Float minusError = null;
        if (minusErrorStr != null) {
            minusError = Float.parseFloat(minusErrorStr);
        }
    }
    
    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.VALUE)) {
            coefficient = StaxUtil.pullFloat(reader, tagName);
            return true;
        } else {
            return false;
        }
    }
    
    public FloatNoUnitType(float coefficient, Float plusError, Float minusError) {
        this.coefficient = coefficient;
        this.plusError = plusError;
        this.minusError = minusError;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    public void setPlusError(Float plusError) {
        this.plusError = plusError;
    }

    public void setMinusError(Float minusError) {
        this.minusError = minusError;
    }

    String tagName;
    
    float coefficient;

    Float plusError, minusError;
}
