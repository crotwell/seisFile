package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class ConfidenceEllipsoid {

    public static final String ELEMENT_NAME = QuakeMLTagNames.confidenceEllipsoid;

    public ConfidenceEllipsoid(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.semiMajorAxisLength)) {
                    semiMajorAxisLength = Float.parseFloat(StaxUtil.pullText(reader,
                                                                             QuakeMLTagNames.semiMajorAxisLength));
                } else if (elName.equals(QuakeMLTagNames.semiMinorAxisLength)) {
                    semiMinorAxisLength = Float.parseFloat(StaxUtil.pullText(reader,
                                                                             QuakeMLTagNames.semiMinorAxisLength));
                } else if (elName.equals(QuakeMLTagNames.semiIntermediateAxisLength)) {
                    semiIntermediateAxisLength = Float.parseFloat(StaxUtil.pullText(reader,
                                                                                    QuakeMLTagNames.semiIntermediateAxisLength));
                } else if (elName.equals(QuakeMLTagNames.majorAxisPlunge)) {
                    majorAxisPlunge = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.majorAxisPlunge));
                } else if (elName.equals(QuakeMLTagNames.majorAxisAzimuth)) {
                    majorAxisAzimuth = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.majorAxisAzimuth));
                } else if (elName.equals(QuakeMLTagNames.majorAxisRotation)) {
                    majorAxisRotation = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.majorAxisRotation));
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

    public Float getMajorAxisAzimuth() {
        return majorAxisAzimuth;
    }

    public Float getMajorAxisPlunge() {
        return majorAxisPlunge;
    }

    public Float getMajorAxisRotation() {
        return majorAxisRotation;
    }

    public Float getSemiIntermediateAxisLength() {
        return semiIntermediateAxisLength;
    }

    public Float getSemiMajorAxisLength() {
        return semiMajorAxisLength;
    }

    public Float getSemiMinorAxisLength() {
        return semiMinorAxisLength;
    }

    public void setMajorAxisAzimuth(Float majorAxisAzimuth) {
        this.majorAxisAzimuth = majorAxisAzimuth;
    }

    public void setMajorAxisPlunge(Float majorAxisPlunge) {
        this.majorAxisPlunge = majorAxisPlunge;
    }

    public void setMajorAxisRotation(Float majorAxisRotation) {
        this.majorAxisRotation = majorAxisRotation;
    }

    public void setSemiIntermediateAxisLength(Float semiIntermediateAxisLength) {
        this.semiIntermediateAxisLength = semiIntermediateAxisLength;
    }

    public void setSemiMajorAxisLength(Float semiMajorAxisLength) {
        this.semiMajorAxisLength = semiMajorAxisLength;
    }

    public void setSemiMinorAxisLength(Float semiMinorAxisLength) {
        this.semiMinorAxisLength = semiMinorAxisLength;
    }

    Float semiMajorAxisLength;

    Float semiMinorAxisLength;

    Float semiIntermediateAxisLength;

    Float majorAxisPlunge;

    Float majorAxisAzimuth;

    Float majorAxisRotation;
}
