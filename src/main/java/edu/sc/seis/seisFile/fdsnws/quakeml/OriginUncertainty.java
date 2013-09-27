package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class OriginUncertainty {

    public static final String ELEMENT_NAME = QuakeMLTagNames.originUncertainty;

    public OriginUncertainty(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.horizontalUncertainty)) {
                    horizontalUncertainty = Float.parseFloat(StaxUtil.pullText(reader,
                                                                               QuakeMLTagNames.horizontalUncertainty));
                } else if (elName.equals(QuakeMLTagNames.minHorizontalUncertainty)) {
                    minHorizontalUncertainty = Float.parseFloat(StaxUtil.pullText(reader,
                                                                                  QuakeMLTagNames.minHorizontalUncertainty));
                } else if (elName.equals(QuakeMLTagNames.maxHorizontalUncertainty)) {
                    maxHorizontalUncertainty = Float.parseFloat(StaxUtil.pullText(reader,
                                                                                  QuakeMLTagNames.maxHorizontalUncertainty));
                } else if (elName.equals(QuakeMLTagNames.azimuthMaxHorizontalUncertainty)) {
                    azimuthMaxHorizontalUncertainty = Float.parseFloat(StaxUtil.pullText(reader,
                                                                                         QuakeMLTagNames.azimuthMaxHorizontalUncertainty));
                } else if (elName.equals(QuakeMLTagNames.confidenceLevel)) {
                    confidenceLevel = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.confidenceLevel));
                } else if (elName.equals(QuakeMLTagNames.confidenceEllipsoid)) {
                    confidenceEllipsoid = new ConfidenceEllipsoid(reader);
                } else if (elName.equals(QuakeMLTagNames.preferredDescription)) {
                    preferredDescription = StaxUtil.pullText(reader, QuakeMLTagNames.preferredDescription);
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

    public Float getHorizontalUncertainty() {
        return horizontalUncertainty;
    }

    public Float getMinHorizontalUncertainty() {
        return minHorizontalUncertainty;
    }

    public Float getMaxHorizontalUncertainty() {
        return maxHorizontalUncertainty;
    }

    public Float getAzimuthMaxHorizontalUncertainty() {
        return azimuthMaxHorizontalUncertainty;
    }

    public ConfidenceEllipsoid getConfidenceEllipsoid() {
        return confidenceEllipsoid;
    }

    public Float getConfidenceLevel() {
        return confidenceLevel;
    }

    public String getPreferredDescription() {
        return preferredDescription;
    }

    Float horizontalUncertainty;

    Float minHorizontalUncertainty;

    Float maxHorizontalUncertainty;

    Float azimuthMaxHorizontalUncertainty;

    ConfidenceEllipsoid confidenceEllipsoid;

    Float confidenceLevel;

    String preferredDescription;
}
