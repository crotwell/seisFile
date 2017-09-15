package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class HertzFloatType extends FloatType {

    public HertzFloatType() {
        setUnit(DEFAULT_UNIT);
    }
    
    public HertzFloatType(float value, Float plusError, Float minusError) {
        super(value, DEFAULT_UNIT, plusError, minusError);
    }

    public HertzFloatType(float value) {
        super(value, DEFAULT_UNIT);
    }

    public HertzFloatType(XMLEventReader reader, String tagName) throws StationXMLException, XMLStreamException {
        super(reader, tagName);
        setUnit(DEFAULT_UNIT);
    }

    public static final String DEFAULT_UNIT = Unit.HERTZ;

}
