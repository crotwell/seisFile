package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class Pole extends PoleZero {

    public Pole(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.POLE);
    }
}
