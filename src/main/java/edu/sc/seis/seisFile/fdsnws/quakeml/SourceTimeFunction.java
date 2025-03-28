package edu.sc.seis.seisFile.fdsnws.quakeml;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class SourceTimeFunction {


    public static final String ELEMENT_NAME = QuakeMLTagNames.sourceTimeFunction;

    public SourceTimeFunction(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.duration)) {
                    duration = StaxUtil.pullFloat(reader, QuakeMLTagNames.duration);
                } else if (elName.equals(QuakeMLTagNames.riseTime)) {
                    riseTime = StaxUtil.pullFloat(reader, QuakeMLTagNames.riseTime);
                } else if (elName.equals(QuakeMLTagNames.decayTime)) {
                    decayTime = StaxUtil.pullFloat(reader, QuakeMLTagNames.decayTime);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getRiseTime() {
        return riseTime;
    }

    public void setRiseTime(Float riseTime) {
        this.riseTime = riseTime;
    }

    public Float getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(Float decayTime) {
        this.decayTime = decayTime;
    }

    String type;

    Float duration;

    Float riseTime;

    Float decayTime;
}
