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

    public String getPublicId() {
        return publicId;
    }

    public Time getTime() {
        return time;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public RealQuantity getBackazimuth() {
        return backazimuth;
    }

    public String getFilterID() {
        return filterID;
    }

    public String getOnset() {
        return onset;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getSlownessMethodID() {
        return slownessMethodID;
    }

    public String getPhaseHint() {
        return phaseHint;
    }

    public String getPolarity() {
        return polarity;
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

    public List<Comment> getCommentList() {
        return commentList;
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
