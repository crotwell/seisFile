package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class NetworkIterator {

    public NetworkIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        StaxUtil.skipToStartElement(reader);
        return reader.hasNext() && reader.peek().isStartElement()
                && reader.peek().asStartElement().getName().getLocalPart().equals(StationXMLTagNames.NETWORK);
    }

    public Network next() throws XMLStreamException, StationXMLException {
        return new Network(reader);
    }

    XMLEventReader reader;
}
