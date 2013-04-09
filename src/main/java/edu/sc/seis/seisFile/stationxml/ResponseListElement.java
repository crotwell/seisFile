package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class ResponseListElement {

    
    public ResponseListElement(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.RESPONSELISTELEMENT, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.FREQUENCY)) {
                    frequency = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCY);
                } else if (elName.equals(StationXMLTagNames.AMPLITUDE)) {
                    amplitude = StaxUtil.pullFloat(reader, StationXMLTagNames.AMPLITUDE);
                } else if (elName.equals(StationXMLTagNames.PHASE)) {
                    phase = StaxUtil.pullFloat(reader, StationXMLTagNames.PHASE);
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else  {
                e = reader.nextEvent();
            }
        }
    }
    
    
    public float getFrequency() {
        return frequency;
    }

    
    public float getAmplitude() {
        return amplitude;
    }

    
    public float getPhase() {
        return phase;
    }

    private float frequency, amplitude, phase;
}
