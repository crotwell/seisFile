package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class StationMagnitudeContribution {

    public static final String ELEMENT_NAME = QuakeMLTagNames.stationMagnitudeContribution;

    public StationMagnitudeContribution(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.stationMagnitudeID)) {
                    stationMagnitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.stationMagnitudeID);
                } else if (elName.equals(QuakeMLTagNames.residual)) {
                    residual = StaxUtil.pullFloat(reader, QuakeMLTagNames.residual);
                } else if (elName.equals(QuakeMLTagNames.weight)) {
                    weight = StaxUtil.pullFloat(reader, QuakeMLTagNames.weight);
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

    public String getStationMagnitudeID() {
        return stationMagnitudeID;
    }

    public float getResidual() {
        return residual;
    }

    public float getWeight() {
        return weight;
    }

    private String stationMagnitudeID;

    private float residual;

    private float weight;
}
