package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

@Deprecated
public class Sensor extends Equipment {

    public Sensor(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.SENSOR);
    }

}
