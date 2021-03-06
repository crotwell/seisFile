package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class InstrumentSensitivity extends GainSensitivity {

    public InstrumentSensitivity() {
        // hibernate
    }
    
    public InstrumentSensitivity(float value,
                                 float frequency,
                                 Unit inputUnits,
                                 Unit outputUnits,
                                 float frequencyStart,
                                 float frequencyEnd,
                                 float frequencyDbVariation) {
        super(value, frequency);
        this.inputUnits = inputUnits;
        this.outputUnits = outputUnits;
        this.frequencyStart = frequencyStart;
        this.frequencyEnd = frequencyEnd;
        this.frequencyDbVariation = frequencyDbVariation;
    }


    public InstrumentSensitivity(float value, float frequency) {
        super(value, frequency);
    }
    

    public InstrumentSensitivity(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.INSTRUMENT_SENSITIVITY, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
                } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = new Unit(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = new Unit(reader, StationXMLTagNames.OUTPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.FREQUENCYSTART)) {
                    frequencyStart = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCYSTART);
                } else if (elName.equals(StationXMLTagNames.FREQUENCYEND)) {
                    frequencyEnd = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCYEND);
                } else if (elName.equals(StationXMLTagNames.FREQUENCYDBVARIATION)) {
                    frequencyDbVariation = StaxUtil.pullFloat(reader, StationXMLTagNames.FREQUENCYDBVARIATION);
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

    public Unit getInputUnits() {
        return inputUnits;
    }

    public Unit getOutputUnits() {
        return outputUnits;
    }

    public float getFrequencyStart() {
        return frequencyStart;
    }

    public float getFrequencyEnd() {
        return frequencyEnd;
    }

    public float getFrequencyDbVariation() {
        return frequencyDbVariation;
    }

    public void setInputUnits(Unit inputUnits) {
        this.inputUnits = inputUnits;
    }

    public void setOutputUnits(Unit outputUnits) {
        this.outputUnits = outputUnits;
    }

    public void setFrequencyStart(float frequencyStart) {
        this.frequencyStart = frequencyStart;
    }

    public void setFrequencyEnd(float frequencyEnd) {
        this.frequencyEnd = frequencyEnd;
    }

    public void setFrequencyDbVariation(float frequencyDbVariation) {
        this.frequencyDbVariation = frequencyDbVariation;
    }

    public static boolean isValid(InstrumentSensitivity sens) {
        return sens != null && sens.getFrequency() >= 0 && sens.getSensitivityValue() != -1;
    }

    Unit inputUnits, outputUnits;

    float frequencyStart, frequencyEnd, frequencyDbVariation;
}
