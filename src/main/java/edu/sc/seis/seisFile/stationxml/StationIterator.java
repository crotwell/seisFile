package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class StationIterator {

    public StationIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        StaxUtil.skipToStartElement(reader);
        return reader.hasNext() && reader.peek().isStartElement()
                && reader.peek().asStartElement().getName().getLocalPart().equals(StaMessage.STATION);
    }

    public Station next() throws XMLStreamException, StationXMLException {
        return new Station(reader);
    }

    XMLEventReader reader;
}
