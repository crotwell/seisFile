package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class NetworkIterator {

    public NetworkIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, StationXMLTagNames.NETWORK, StationXMLTagNames.STAMESSAGE);
    }

    public Network next() throws XMLStreamException, StationXMLException {
        return new Network(reader);
    }

    XMLEventReader reader;
}
