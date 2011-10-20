package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class Zero extends PoleZero {

    public Zero(XMLEventReader reader) throws XMLStreamException, StationXMLException {
              super(reader, StationXMLTagNames.ZERO);
    }
}
