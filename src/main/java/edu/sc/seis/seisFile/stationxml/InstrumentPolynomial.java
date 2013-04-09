package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class InstrumentPolynomial extends Polynomial {

    public InstrumentPolynomial(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader);
    }
}
