package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class StationIterator {

    protected StationIterator() {}
    
    public StationIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, StationXMLTagNames.STATION, StationXMLTagNames.NETWORK);
    }

    public Station next() throws XMLStreamException, StationXMLException {
        return new Station(reader);
    }

    XMLEventReader reader;
}
