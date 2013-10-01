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

    public String getPublicId() {
        return publicId;
    }

    public String getOriginId() {
        return originId;
    }

    public String getType() {
        return type;
    }

    public RealQuantity getMag() {
        return mag;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public String getMethodID() {
        return methodID;
    }

    public Integer getStationCount() {
        return stationCount;
    }

    public Float getAzimuthalGap() {
        return azimuthalGap;
    }

    public List<StationMagnitudeContribution> getStationMagnitudeContributionList() {
        return stationMagnitudeContributionList;
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
