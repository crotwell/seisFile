package edu.sc.seis.seisFile.fdsnws;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;


public interface StaxElementProcessor {
    
    public void processNextStartElement(XMLEventReader reader) throws XMLStreamException, SeisFileException;
}
