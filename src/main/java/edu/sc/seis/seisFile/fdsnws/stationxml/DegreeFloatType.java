package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/**
 * FloatType where unit is fixed as DEGREE
 *
 */
public class DegreeFloatType extends FloatType {

    DegreeFloatType() {
        setUnit(DEFAULT_UNIT);
    }

    public DegreeFloatType(float value, Float plusError, Float minusError) {
        super(value, DEFAULT_UNIT, plusError, minusError);
    }

    public DegreeFloatType(float value) {
        super(value, DEFAULT_UNIT);
    }

    public DegreeFloatType(XMLEventReader reader, String tagName) throws StationXMLException, XMLStreamException {
        super(reader, tagName);
        setUnit(DEFAULT_UNIT);
    }

    public static final String DEFAULT_UNIT = Unit.DEGREE;
}
