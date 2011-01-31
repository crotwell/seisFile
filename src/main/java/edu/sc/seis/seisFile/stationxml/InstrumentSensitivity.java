package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class InstrumentSensitivity {
    

    public InstrumentSensitivity(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(Epoch.INSTRUMENT_SENSITIVITY)) {
            XMLEvent e = reader.nextEvent(); // pop Site
            while (reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals(SENSITIVITY_VALUE)) {
                        sensitivityValue = StaxUtil.pullText(reader, SENSITIVITY_VALUE);
                    } else if (elName.equals(FREQUENCY)) {
                        frequency = StaxUtil.pullText(reader, FREQUENCY);
                    } else if (elName.equals(SENSITIVITY_UNITS)) {
                        sensitivityUnits = StaxUtil.pullText(reader, SENSITIVITY_UNITS);
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
        } else {
            throw new StationXMLException("Not a "+Epoch.INSTRUMENT_SENSITIVITY+" element: " + cur.asStartElement().getName().getLocalPart());
        }
    }
    
    String sensitivityValue, frequency, sensitivityUnits;

    public static final String SENSITIVITY_VALUE = "SensitivityValue";
    public static final String FREQUENCY = "Frequency";
    public static final String SENSITIVITY_UNITS = "SensitivityUnits";
}
