package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class CoefficientWithError {
    
    public CoefficientWithError(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);

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
        coefficient = StaxUtil.pullFloat(reader, tagName);
    

            }

    public CoefficientWithError(float coefficient, Float plusError, Float minusError) {
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

    float coefficient;

    Float plusError, minusError;
}
