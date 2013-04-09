package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class DataLogger extends Equipment {

    public DataLogger(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader);
    }
}
