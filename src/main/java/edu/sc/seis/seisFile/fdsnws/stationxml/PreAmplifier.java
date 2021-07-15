package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

@Deprecated
public class PreAmplifier extends Equipment {

    public PreAmplifier(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        super(reader, StationXMLTagNames.PREAMPLIFIER);
    }
}
