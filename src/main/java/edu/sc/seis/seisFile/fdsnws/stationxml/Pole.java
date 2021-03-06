package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class Pole extends PoleZero {

    public Pole(FloatNoUnitType realWithError, FloatNoUnitType imaginaryWithError) {
        super(realWithError, imaginaryWithError);
    }

    public Pole(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.POLE);
    }
}
