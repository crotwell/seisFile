package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxElementProcessor;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;


public class EventIterator {
    
    public EventIterator(XMLEventReader reader, EventParameters parent) {
        this.reader = reader;
        this.parent = parent;
    }

    public boolean hasNext() throws XMLStreamException, SeisFileException {
        return StaxUtil.hasNext(reader, QuakeMLTagNames.event,
                                QuakeMLTagNames.QUAKEML,
                                new StaxElementProcessor() {
            
            @Override
            public void processNextStartElement(XMLEventReader reader) throws XMLStreamException, SeisFileException {
                parent.processOneStartElement(reader);
            }
        });
    }

    public Event next() throws XMLStreamException, SeisFileException {
        if (hasNext()) { // side effect, make sure hasNext was called to skip over any non-Event elements
            return new Event(reader);
        } else {
            return null;
        }
    }

    XMLEventReader reader;
    
    final EventParameters parent;
}
