package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class TimeWindow {
    
    public static final String ELEMENT_NAME = QuakeMLTagNames.timeWindow;

    public TimeWindow(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.reference)) {
                    reference = StaxUtil.pullText(reader, QuakeMLTagNames.reference);
                } else if (elName.equals(QuakeMLTagNames.begin)) {
                    begin = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.begin));
                } else if (elName.equals(QuakeMLTagNames.end)) {
                    end = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.end));
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }
    
    
    public Float getBegin() {
        return begin;
    }

    
    public Float getEnd() {
        return end;
    }

    
    public String getReference() {
        return reference;
    }

    Float begin;
    
    Float end;
    
    String reference;
}
