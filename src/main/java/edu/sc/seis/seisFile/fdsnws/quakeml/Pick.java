package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Pick {

    public Pick(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.pick, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.waveformID)) {
                    waveformID = new WaveformStreamID(reader, QuakeMLTagNames.waveformID);
                } else if (elName.equals(QuakeMLTagNames.time)) {
                    time = new Time(reader, QuakeMLTagNames.time);
                } else if (elName.equals(QuakeMLTagNames.backazimuth)) {
                    backazimuth = new RealQuantity(reader, QuakeMLTagNames.backazimuth);
                } else if (elName.equals(QuakeMLTagNames.filterID)) {
                    filterID = StaxUtil.pullText(reader, QuakeMLTagNames.filterID);
                } else if (elName.equals(QuakeMLTagNames.onset)) {
                    onset = StaxUtil.pullText(reader, QuakeMLTagNames.onset);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
                } else if (elName.equals(QuakeMLTagNames.slownessMethodID)) {
                    slownessMethodID = StaxUtil.pullText(reader, QuakeMLTagNames.slownessMethodID);
                } else if (elName.equals(QuakeMLTagNames.phaseHint)) {
                    phaseHint = StaxUtil.pullText(reader, QuakeMLTagNames.phaseHint);
                } else if (elName.equals(QuakeMLTagNames.polarity)) {
                    polarity = StaxUtil.pullText(reader, QuakeMLTagNames.polarity);
                } else if (elName.equals(QuakeMLTagNames.evaluationMode)) {
                    evaluationMode = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationMode);
                } else if (elName.equals(QuakeMLTagNames.evaluationStatus)) {
                    evaluationStatus = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationStatus);
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

    public RealQuantity getBackazimuth() {
        return backazimuth;
    }

    public List<Comment> getCommentList() {
        return commentList;
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

    public String getFilterID() {
        return filterID;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getOnset() {
        return onset;
    }

    public String getPhaseHint() {
        return phaseHint;
    }

    public String getPolarity() {
        return polarity;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSlownessMethodID() {
        return slownessMethodID;
    }

    public Time getTime() {
        return time;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public void setBackazimuth(RealQuantity backazimuth) {
        this.backazimuth = backazimuth;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
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

    public void setFilterID(String filterID) {
        this.filterID = filterID;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public void setPhaseHint(String phaseHint) {
        this.phaseHint = phaseHint;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setSlownessMethodID(String slownessMethodID) {
        this.slownessMethodID = slownessMethodID;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setWaveformID(WaveformStreamID waveformID) {
        this.waveformID = waveformID;
    }

    private String publicId;

    private Time time;

    private WaveformStreamID waveformID;

    private RealQuantity backazimuth;

    private String filterID;

    private String onset;

    private String methodID;

    private String slownessMethodID;

    private String phaseHint;

    private String polarity;

    private String evaluationMode;

    private String evaluationStatus;

    private CreationInfo creationInfo;

    private List<Comment> commentList = new ArrayList<Comment>();
}
