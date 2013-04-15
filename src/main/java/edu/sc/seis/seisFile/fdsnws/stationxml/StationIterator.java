package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;

public class StationIterator {

    protected StationIterator() {}
    
    public StationIterator(XMLEventReader reader, String networkCode) {
        this.reader = reader;
        this.networkCode = networkCode;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, StationXMLTagNames.STATION, StationXMLTagNames.NETWORK);
    }

    public Station next() throws XMLStreamException, StationXMLException {
        return new Station(reader, networkCode);
    }

    XMLEventReader reader;
    
    String networkCode;
}
