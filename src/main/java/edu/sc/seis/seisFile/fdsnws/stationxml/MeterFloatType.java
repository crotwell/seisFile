package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class MeterFloatType extends FloatType {

    public MeterFloatType() {
        setUnit(DEFAULT_UNIT);
    }

    public MeterFloatType(float value, Float plusError, Float minusError) {
        super(value, DEFAULT_UNIT, plusError, minusError);
    }

    public MeterFloatType(float value) {
        super(value, DEFAULT_UNIT);
    }

    public MeterFloatType(XMLEventReader reader, String tagName) throws StationXMLException, XMLStreamException {
        super(reader, tagName);
        setUnit(DEFAULT_UNIT);
    }

    public static final String DEFAULT_UNIT = Unit.METER;

    
}
