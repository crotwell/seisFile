package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class InstrumentSensitivity {

    public InstrumentSensitivity(float value, String unit, float frequency) {
        this.sensitivityValue = value;
        this.sensitivityUnits = unit;
        this.frequency = frequency;
    }

    public InstrumentSensitivity(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.INSTRUMENT_SENSITIVITY, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.SENSITIVITY_VALUE)) {
                    sensitivityValue = StaxUtil.pullFloat(reader, StationXMLTagNames.SENSITIVITY_VALUE);
                } else if (elName.equals(StationXMLTagNames.FREQUENCY)) {
                    frequency = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCY);
                } else if (elName.equals(StationXMLTagNames.SENSITIVITY_UNITS)) {
                    sensitivityUnits = StaxUtil.pullText(reader, StationXMLTagNames.SENSITIVITY_UNITS);
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

    public float getSensitivityValue() {
        return sensitivityValue;
    }

    public float getFrequency() {
        return frequency;
    }

    public String getSensitivityUnits() {
        return sensitivityUnits;
    }

    float sensitivityValue, frequency;

    String sensitivityUnits;
}
