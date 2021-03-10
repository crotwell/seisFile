package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class Zero extends PoleZero {

    public Zero(FloatNoUnitType realWithError, FloatNoUnitType imaginaryWithError) {
        super(realWithError, imaginaryWithError);
    }

    public Zero(XMLEventReader reader) throws XMLStreamException, StationXMLException {
              super(reader, StationXMLTagNames.ZERO);
    }
}
