package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Time {

    public Time(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.time, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.value)) {
                    value = StaxUtil.pullText(reader, QuakeMLTagNames.value);
                } else if (elName.equals(QuakeMLTagNames.uncertainty)) {
                    uncertainty = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.uncertainty));
                } else if (elName.equals(QuakeMLTagNames.lowerUncertainty)) {
                    lowerUncertainty = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.lowerUncertainty));
                } else if (elName.equals(QuakeMLTagNames.upperUncertainty)) {
                    upperUncertainty = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.upperUncertainty));
                } else if (elName.equals(QuakeMLTagNames.confidenceLevel)) {
                    confidenceLevel = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.confidenceLevel));
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

    public String getValue() {
        return value;
    }

    public Float getUncertainty() {
        return uncertainty;
    }

    public Float getLowerUncertainty() {
        return lowerUncertainty;
    }

    public Float getUpperUncertainty() {
        return upperUncertainty;
    }

    public Float getConfidenceLevel() {
        return confidenceLevel;
    }

    String value;

    Float uncertainty;

    Float lowerUncertainty;

    Float upperUncertainty;

    Float confidenceLevel;
}
