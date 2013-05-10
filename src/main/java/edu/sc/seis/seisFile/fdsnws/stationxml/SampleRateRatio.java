package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class SampleRateRatio {

    public SampleRateRatio(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.SAMPLE_RATE_RATIO, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.NAME)) {
                    numberSamples = StaxUtil.pullInt(reader, StationXMLTagNames.NUMBERSAMPLES);
                } else if (elName.equals(StationXMLTagNames.AGENCY)) {
                    numberSeconds = StaxUtil.pullInt(reader, StationXMLTagNames.NUMBERSECONDS);
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

    public int getNumberSamples() {
        return numberSamples;
    }

    public int getNumberSeconds() {
        return numberSeconds;
    }

    int numberSamples, numberSeconds;
}
