package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Magnitude {

    public static final String ELEMENT_NAME = QuakeMLTagNames.magnitude;

    public Magnitude() {
        this.publicId = "autogen"+Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public Magnitude(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.originID)) {
                    originId = StaxUtil.pullText(reader, QuakeMLTagNames.originID);
                } else if (elName.equals(QuakeMLTagNames.stationCount)) {
                    stationCount = StaxUtil.pullInt(reader, QuakeMLTagNames.stationCount);
                } else if (elName.equals(QuakeMLTagNames.azimuthalGap)) {
                    azimuthalGap = StaxUtil.pullFloat(reader, QuakeMLTagNames.azimuthalGap);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.mag)) {
                    mag = new RealQuantity(reader, QuakeMLTagNames.mag);
                } else if (elName.equals(QuakeMLTagNames.stationMagnitudeContribution)) {
                    stationMagnitudeContributionList.add(new StationMagnitudeContribution(reader));
                } else if (elName.equals(QuakeMLTagNames.evaluationMode)) {
                    evaluationMode = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationMode);
                } else if (elName.equals(QuakeMLTagNames.evaluationStatus)) {
                    evaluationStatus = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationStatus);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
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

    public Float getAzimuthalGap() {
        return azimuthalGap;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public RealQuantity getMag() {
        return mag;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getOriginId() {
        return originId;
    }

    public String getPublicId() {
        return publicId;
    }

    public Integer getStationCount() {
        return stationCount;
    }

    public List<StationMagnitudeContribution> getStationMagnitudeContributionList() {
        return stationMagnitudeContributionList;
    }

    public String getType() {
        return type;
    }

    public void setAzimuthalGap(Float azimuthalGap) {
        this.azimuthalGap = azimuthalGap;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
    }

    public void setEvaluationMode(String evaluationMode) {
        this.evaluationMode = evaluationMode;
    }

    public void setEvaluationStatus(String evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public void setMag(RealQuantity mag) {
        this.mag = mag;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setStationCount(Integer stationCount) {
        this.stationCount = stationCount;
    }

    public void setStationMagnitudeContributionList(List<StationMagnitudeContribution> stationMagnitudeContributionList) {
        this.stationMagnitudeContributionList = stationMagnitudeContributionList;
    }

    public void setType(String type) {
        this.type = type;
    }

    String publicId;

    String originId;

    String type;

    Integer stationCount;

    Float azimuthalGap;

    RealQuantity mag;

    List<StationMagnitudeContribution> stationMagnitudeContributionList = new ArrayList<StationMagnitudeContribution>();

    String evaluationMode;

    String evaluationStatus;

    String methodID;

    CreationInfo creationInfo;
}
