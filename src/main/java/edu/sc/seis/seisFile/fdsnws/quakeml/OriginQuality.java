package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class OriginQuality {

    public static final String ELEMENT_NAME = QuakeMLTagNames.quality;

    public OriginQuality(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.associatedPhaseCount)) {
                    associatedPhaseCount = Integer.parseInt(StaxUtil.pullText(reader,
                                                                              QuakeMLTagNames.associatedPhaseCount));
                } else if (elName.equals(QuakeMLTagNames.usedPhaseCount)) {
                    usedPhaseCount = Integer.parseInt(StaxUtil.pullText(reader, QuakeMLTagNames.usedPhaseCount));
                } else if (elName.equals(QuakeMLTagNames.associatedStationCount)) {
                    associatedStationCount = Integer.parseInt(StaxUtil.pullText(reader,
                                                                                QuakeMLTagNames.associatedStationCount));
                } else if (elName.equals(QuakeMLTagNames.usedStationCount)) {
                    usedStationCount = Integer.parseInt(StaxUtil.pullText(reader, QuakeMLTagNames.usedStationCount));
                } else if (elName.equals(QuakeMLTagNames.depthPhaseCount)) {
                    depthPhaseCount = Integer.parseInt(StaxUtil.pullText(reader, QuakeMLTagNames.depthPhaseCount));
                } else if (elName.equals(QuakeMLTagNames.standardError)) {
                    standardError = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.standardError));
                } else if (elName.equals(QuakeMLTagNames.azimuthalGap)) {
                    azimuthalGap = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.azimuthalGap));
                } else if (elName.equals(QuakeMLTagNames.secondaryAzimuthalGap)) {
                    secondaryAzimuthalGap = Float.parseFloat(StaxUtil.pullText(reader,
                                                                               QuakeMLTagNames.secondaryAzimuthalGap));
                } else if (elName.equals(QuakeMLTagNames.maximumDistance)) {
                    maximumDistance = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.maximumDistance));
                } else if (elName.equals(QuakeMLTagNames.minimumDistance)) {
                    minimumDistance = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.minimumDistance));
                } else if (elName.equals(QuakeMLTagNames.medianDistance)) {
                    medianDistance = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.medianDistance));
                } else if (elName.equals(QuakeMLTagNames.groundTruthLevel)) {
                    groundTruthLevel = StaxUtil.pullText(reader, QuakeMLTagNames.groundTruthLevel);
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

    public Integer getAssociatedPhaseCount() {
        return associatedPhaseCount;
    }

    public Integer getAssociatedStationCount() {
        return associatedStationCount;
    }

    public Float getAzimuthalGap() {
        return azimuthalGap;
    }

    public Integer getDepthPhaseCount() {
        return depthPhaseCount;
    }

    public String getGroundTruthLevel() {
        return groundTruthLevel;
    }

    public Float getMaximumDistance() {
        return maximumDistance;
    }

    public Float getMedianDistance() {
        return medianDistance;
    }

    public Float getMinimumDistance() {
        return minimumDistance;
    }

    public Float getSecondaryAzimuthalGap() {
        return secondaryAzimuthalGap;
    }

    public Float getStandardError() {
        return standardError;
    }

    public Integer getUsedPhaseCount() {
        return usedPhaseCount;
    }

    public Integer getUsedStationCount() {
        return usedStationCount;
    }

    public void setAssociatedPhaseCount(Integer associatedPhaseCount) {
        this.associatedPhaseCount = associatedPhaseCount;
    }

    public void setAssociatedStationCount(Integer associatedStationCount) {
        this.associatedStationCount = associatedStationCount;
    }

    public void setAzimuthalGap(Float azimuthalGap) {
        this.azimuthalGap = azimuthalGap;
    }

    public void setDepthPhaseCount(Integer depthPhaseCount) {
        this.depthPhaseCount = depthPhaseCount;
    }

    public void setGroundTruthLevel(String groundTruthLevel) {
        this.groundTruthLevel = groundTruthLevel;
    }

    public void setMaximumDistance(Float maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public void setMedianDistance(Float medianDistance) {
        this.medianDistance = medianDistance;
    }

    public void setMinimumDistance(Float minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public void setSecondaryAzimuthalGap(Float secondaryAzimuthalGap) {
        this.secondaryAzimuthalGap = secondaryAzimuthalGap;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public void setUsedPhaseCount(Integer usedPhaseCount) {
        this.usedPhaseCount = usedPhaseCount;
    }

    public void setUsedStationCount(Integer usedStationCount) {
        this.usedStationCount = usedStationCount;
    }

    Integer associatedPhaseCount;

    Integer usedPhaseCount;

    Integer associatedStationCount;

    Integer usedStationCount;

    Integer depthPhaseCount;

    Float standardError;

    Float azimuthalGap;

    Float secondaryAzimuthalGap;

    Float maximumDistance;

    Float minimumDistance;

    Float medianDistance;

    String groundTruthLevel;
}
