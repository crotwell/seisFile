package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class FloatNoUnitType {

    /** for subclasses when parsing */
    FloatNoUnitType()  {
    }

    public FloatNoUnitType(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        parseAttributes(startE);
        parseValue(reader);
    }

    void parseAttributes(StartElement startE) throws StationXMLException {
        String plusErrorStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.PLUSERROR);
        String minusErrorStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.MINUSERROR);
        if (plusErrorStr != null) {
            plusError = Float.parseFloat(plusErrorStr);
        }
        if (minusErrorStr != null) {
            minusError = Float.parseFloat(minusErrorStr);
        }
    }

    void parseValue(final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        value = Float.parseFloat(StaxUtil.pullContiguousText(reader));
    }

    public FloatNoUnitType(float value, Float plusError, Float minusError) {
        this.value = value;
        this.plusError = plusError;
        this.minusError = minusError;
    }


    public float getValue() {
        return value;
    }

    public Float getPlusError() {
        return plusError;
    }

    public Float getMinusError() {
        return minusError;
    }

    public boolean hasPlusError() {
        return plusError != null;
    }

    public boolean hasMinusError() {
        return minusError != null;
    }

    void setValue(float v) {
        this.value = v;
    }

    float value;

    Float plusError, minusError;
}
