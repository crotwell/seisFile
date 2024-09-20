package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Time {

    public Time(final XMLEventReader reader, String elementName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
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
                reader.nextEvent();
            }
        }
    }

    public Time(Instant t) {
        this.value = TimeUtils.toISOString(t);
    }

    public Float getConfidenceLevel() {
        return confidenceLevel;
    }

    public Float getLowerUncertainty() {
        return lowerUncertainty;
    }

    public Float getUncertainty() {
        return uncertainty;
    }

    public Float getUpperUncertainty() {
        return upperUncertainty;
    }

    public String getValue() {
        return value;
    }

    public Instant asInstant() {
        return TimeUtils.parseISOString(getValue());
    }

    public ZonedDateTime asUTCDateTime() {
        return asInstant().atZone(ZoneId.of("UTC"));
    }
    public void setConfidenceLevel(Float confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public void setLowerUncertainty(Float lowerUncertainty) {
        this.lowerUncertainty = lowerUncertainty;
    }

    public void setUncertainty(Float uncertainty) {
        this.uncertainty = uncertainty;
    }

    public void setUpperUncertainty(Float upperUncertainty) {
        this.upperUncertainty = upperUncertainty;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value;

    Float uncertainty;

    Float lowerUncertainty;

    Float upperUncertainty;

    Float confidenceLevel;
}
