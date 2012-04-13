package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class GainSensitivity {

    public GainSensitivity(float value, String unit, float frequency) {
        this.sensitivityValue = value;
        this.sensitivityUnits = unit;
        this.frequency = frequency;
    }

    public GainSensitivity(XMLEventReader reader, String elementName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.SENSITIVITY_VALUE)) {
                    sensitivityValue = StaxUtil.pullFloat(reader, StationXMLTagNames.SENSITIVITY_VALUE);
                } else if (elName.equals(StationXMLTagNames.FREQUENCY)) {
                    frequency = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCY);
                } else if (elName.equals(StationXMLTagNames.SENSITIVITYUNITS)) {
                    sensitivityUnits = StaxUtil.pullText(reader, StationXMLTagNames.SENSITIVITYUNITS);
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

    @Deprecated
    /** 
     * renamed getSensitivityUnits
     * @return
     */
    public String getGainUnits() {
        return getSensitivityUnits();
    }

    float sensitivityValue, frequency;

    String sensitivityUnits;}
