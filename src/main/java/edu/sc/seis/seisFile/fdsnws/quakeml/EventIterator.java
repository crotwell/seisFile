package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


public class EventIterator {
    
    public EventIterator(XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() throws XMLStreamException {
        return StaxUtil.hasNext(reader, QuakeMLTagNames.event, QuakeMLTagNames.QUAKEML);
    }

    public Event next() throws XMLStreamException, SeisFileException {
        hasNext(); // side effect, make sure hasNext was called to skip over any non-Event elements
        return new Event(reader);
    }

    XMLEventReader reader;
}
