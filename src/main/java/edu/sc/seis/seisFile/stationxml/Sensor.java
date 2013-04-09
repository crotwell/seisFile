package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Sensor extends Equipment {

    public Sensor(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.SENSOR);
    }

}
