package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class ResponseListElement {

    
    public ResponseListElement(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.RESPONSELISTELEMENT, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.FREQUENCY)) {
                    frequency = new FloatType(reader, StationXMLTagNames.FREQUENCY, "HERTZ");
                } else if (elName.equals(StationXMLTagNames.AMPLITUDE)) {
                    amplitude = new FloatType(reader, StationXMLTagNames.AMPLITUDE);
                } else if (elName.equals(StationXMLTagNames.PHASE)) {
                    phase = new FloatType(reader, StationXMLTagNames.PHASE, "DEGREE");
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
    
    
    public FloatType getFrequency() {
        return frequency;
    }

    
    public FloatType getAmplitude() {
        return amplitude;
    }

    
    public FloatType getPhase() {
        return phase;
    }

    FloatType frequency;
    private FloatType phase;
    FloatType amplitude;
}
