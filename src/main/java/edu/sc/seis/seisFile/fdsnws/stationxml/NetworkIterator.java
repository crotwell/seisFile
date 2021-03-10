package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class NetworkIterator {

    public NetworkIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, StationXMLTagNames.NETWORK, StationXMLTagNames.FDSNSTATIONXML);
    }

    public Network next() throws XMLStreamException, StationXMLException {
        return new Network(reader);
    }

    XMLEventReader reader;
}
