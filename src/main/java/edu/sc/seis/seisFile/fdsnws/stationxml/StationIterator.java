package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class StationIterator {

    protected StationIterator() {}
    
    public StationIterator(XMLEventReader reader, Network network) {
        this.reader = reader;
        this.network = network;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, StationXMLTagNames.STATION, StationXMLTagNames.NETWORK);
    }

    public Station next() throws XMLStreamException, StationXMLException {
        return new Station(reader, network);
    }

    XMLEventReader reader;
    
    Network network;
}
