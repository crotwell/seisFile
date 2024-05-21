package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Arrival {

    public Arrival(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.arrival, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.phase)) {
                    phase = StaxUtil.pullText(reader, QuakeMLTagNames.phase);
                } else if (elName.equals(QuakeMLTagNames.azimuth)) {
                    azimuth = StaxUtil.pullFloat(reader, QuakeMLTagNames.azimuth);
                } else if (elName.equals(QuakeMLTagNames.distance)) {
                    distance = StaxUtil.pullFloat(reader, QuakeMLTagNames.distance);
                } else if (elName.equals(QuakeMLTagNames.takeoffAngle)) {
                    takeoffAngle = new RealQuantity(reader, QuakeMLTagNames.takeoffAngle);
                } else if (elName.equals(QuakeMLTagNames.timeResidual)) {
                    timeResidual = StaxUtil.pullFloat(reader, QuakeMLTagNames.timeResidual);
                } else if (elName.equals(QuakeMLTagNames.horizontalSlownessResidual)) {
                    horizontalSlownessResidual = StaxUtil.pullFloat(reader, QuakeMLTagNames.horizontalSlownessResidual);
                } else if (elName.equals(QuakeMLTagNames.backazimuthResidual)) {
                    backazimuthResidual = StaxUtil.pullFloat(reader, QuakeMLTagNames.backazimuthResidual);
                } else if (elName.equals(QuakeMLTagNames.timeWeight)) {
                    timeWeight = StaxUtil.pullFloat(reader, QuakeMLTagNames.timeWeight);
                } else if (elName.equals(QuakeMLTagNames.backazimuthWeight)) {
                    backazimuthWeight = StaxUtil.pullFloat(reader, QuakeMLTagNames.backazimuthWeight);
                } else if (elName.equals(QuakeMLTagNames.horizontalSlownessWeight)) {
                    horizontalSlownessWeight = StaxUtil.pullFloat(reader, QuakeMLTagNames.horizontalSlownessWeight);
                } else if (elName.equals(QuakeMLTagNames.timeCorrection)) {
                    timeCorrection = StaxUtil.pullFloat(reader, QuakeMLTagNames.timeCorrection);
                } else if (elName.equals(QuakeMLTagNames.earthModelID)) {
                    earthModelID = StaxUtil.pullText(reader, QuakeMLTagNames.earthModelID);
                } else if (elName.equals(QuakeMLTagNames.pickID)) {
                    pickID = StaxUtil.pullText(reader, QuakeMLTagNames.pickID);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
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

    public float getAzimuth() {
        return azimuth;
    }

    public Float getBackazimuthResidual() {
        return backazimuthResidual;
    }

    public Float getBackazimuthWeight() {
        return backazimuthWeight;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public float getDistance() {
        return distance;
    }

    public String getEarthModelID() {
        return earthModelID;
    }

    public Float getHorizontalSlownessResidual() {
        return horizontalSlownessResidual;
    }

    public Float getHorizontalSlownessWeight() {
        return horizontalSlownessWeight;
    }

    public String getPhase() {
        return phase;
    }

    public String getPickID() {
        return pickID;
    }

    public String getPublicId() {
        return publicId;
    }

    public RealQuantity getTakeoffAngle() {
        return takeoffAngle;
    }

    public float getTimeCorrection() {
        return timeCorrection;
    }

    public float getTimeResidual() {
        return timeResidual;
    }

    public Float getTimeWeight() {
        return timeWeight;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public void setBackazimuthResidual(Float backazimuthResidual) {
        this.backazimuthResidual = backazimuthResidual;
    }

    public void setBackazimuthWeight(Float backazimuthWeight) {
        this.backazimuthWeight = backazimuthWeight;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setEarthModelID(String earthModelID) {
        this.earthModelID = earthModelID;
    }

    public void setHorizontalSlownessResidual(Float horizontalSlownessResidual) {
        this.horizontalSlownessResidual = horizontalSlownessResidual;
    }

    public void setHorizontalSlownessWeight(Float horizontalSlownessWeight) {
        this.horizontalSlownessWeight = horizontalSlownessWeight;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setPickID(String pickID) {
        this.pickID = pickID;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setTakeoffAngle(RealQuantity takeoffAngle) {
        this.takeoffAngle = takeoffAngle;
    }

    public void setTimeCorrection(float timeCorrection) {
        this.timeCorrection = timeCorrection;
    }

    public void setTimeResidual(float timeResidual) {
        this.timeResidual = timeResidual;
    }

    public void setTimeWeight(Float timeWeight) {
        this.timeWeight = timeWeight;
    }

    String publicId;

    String phase;

    float azimuth;

    float distance;

    RealQuantity takeoffAngle;

    float timeResidual;

    float timeCorrection;

    Float horizontalSlownessWeight;

    Float backazimuthWeight;

    Float horizontalSlownessResidual;

    Float backazimuthResidual;

    Float timeWeight;

    String earthModelID;

    String pickID;

    CreationInfo creationInfo;

    List<Comment> commentList = new ArrayList<Comment>();
}
