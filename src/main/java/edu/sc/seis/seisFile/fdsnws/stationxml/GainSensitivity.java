package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


public class GainSensitivity {

    GainSensitivity() {}
    
    public GainSensitivity(float value, float frequency) {
        this.sensitivityValue = value;
        this.frequency = frequency;
    }

    public GainSensitivity(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (parseSubElement(elName, reader)) {
                    // super handled it
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
    
    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.VALUE)) {
            sensitivityValue = StaxUtil.pullFloat(reader, StationXMLTagNames.VALUE);
            return true;
        } else if (elName.equals(StationXMLTagNames.FREQUENCY)) {
            frequency = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCY);
            return true;
        } else {
            return false;
        }
    }

    public float getSensitivityValue() {
        return sensitivityValue;
    }

    public float getFrequency() {
        return frequency;
    }

    float sensitivityValue, frequency;
}
