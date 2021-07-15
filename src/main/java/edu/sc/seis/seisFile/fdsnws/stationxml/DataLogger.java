package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

@Deprecated
public class DataLogger extends Equipment {

    public DataLogger(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.DATALOGGER);
    }
}
