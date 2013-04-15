package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.fdsnws.stationxml.PoleZero;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Zero extends PoleZero {

    public Zero(XMLEventReader reader) throws XMLStreamException, StationXMLException {
              super(reader, StationXMLTagNames.ZERO);
    }
}
