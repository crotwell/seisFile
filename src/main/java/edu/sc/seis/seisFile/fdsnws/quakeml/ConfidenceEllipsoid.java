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

    public Float getSemiMajorAxisLength() {
        return semiMajorAxisLength;
    }

    public Float getSemiMinorAxisLength() {
        return semiMinorAxisLength;
    }

    public Float getSemiIntermediateAxisLength() {
        return semiIntermediateAxisLength;
    }

    public Float getMajorAxisPlunge() {
        return majorAxisPlunge;
    }

    public Float getMajorAxisAzimuth() {
        return majorAxisAzimuth;
    }

    public Float getMajorAxisRotation() {
        return majorAxisRotation;
    }

    Float semiMajorAxisLength;

    Float semiMinorAxisLength;

    Float semiIntermediateAxisLength;

    Float majorAxisPlunge;

    Float majorAxisAzimuth;

    Float majorAxisRotation;
}
