package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class PreAmplifier extends Equipment {

    public PreAmplifier(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader);
    }
}
