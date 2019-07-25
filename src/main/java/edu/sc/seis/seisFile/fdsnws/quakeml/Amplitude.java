package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Amplitude {

    public static final String ELEMENT_NAME = QuakeMLTagNames.amplitude;

    public static String getElementName() {
        return ELEMENT_NAME;
    }

    public Amplitude(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicID = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.category)) {
                    category = StaxUtil.pullText(reader, QuakeMLTagNames.category);
                } else if (elName.equals(QuakeMLTagNames.unit)) {
                    unit = StaxUtil.pullText(reader, QuakeMLTagNames.unit);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
                } else if (elName.equals(QuakeMLTagNames.snr)) {
                    snr = StaxUtil.pullFloat(reader, QuakeMLTagNames.snr);
                } else if (elName.equals(QuakeMLTagNames.scalingTime)) {
                    scalingTime = new Time(reader, QuakeMLTagNames.scalingTime);
                } else if (elName.equals(QuakeMLTagNames.timeWindow)) {
                    timeWindow = new TimeWindow(reader);
                } else if (elName.equals(QuakeMLTagNames.waveformID)) {
                    waveformID = new WaveformStreamID(reader, QuakeMLTagNames.waveformID);
                } else if (elName.equals(QuakeMLTagNames.filterID)) {
                    filterID = StaxUtil.pullText(reader, QuakeMLTagNames.filterID);
                } else if (elName.equals(QuakeMLTagNames.magnitudeHint)) {
                    magnitudeHint = StaxUtil.pullText(reader, QuakeMLTagNames.magnitudeHint);
                } else if (elName.equals(QuakeMLTagNames.pickID)) {
                    pickID = StaxUtil.pullText(reader, QuakeMLTagNames.pickID);
                } else if (elName.equals(QuakeMLTagNames.period)) {
                    period = new RealQuantity(reader, QuakeMLTagNames.period);
                } else if (elName.equals(QuakeMLTagNames.evaluationMode)) {
                    evaluationMode = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationMode);
                } else if (elName.equals(QuakeMLTagNames.evaluationStatus)) {
                    evaluationStatus = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationStatus);
                } else if (elName.equals(QuakeMLTagNames.genericAmplitude)) {
                    genericAmplitude = new RealQuantity(reader, QuakeMLTagNames.genericAmplitude);
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

    public String getCategory() {
        return category;
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

    public RealQuantity getGenericAmplitude() {
        return genericAmplitude;
    }

    public String getMagnitudeHint() {
        return magnitudeHint;
    }

    public String getMethodID() {
        return methodID;
    }

    public RealQuantity getPeriod() {
        return period;
    }

    public String getPickID() {
        return pickID;
    }

    public String getPublicID() {
        return publicID;
    }

    public Time getScalingTime() {
        return scalingTime;
    }

    public Float getSnr() {
        return snr;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setGenericAmplitude(RealQuantity genericAmplitude) {
        this.genericAmplitude = genericAmplitude;
    }

    public void setMagnitudeHint(String magnitudeHint) {
        this.magnitudeHint = magnitudeHint;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public void setPeriod(RealQuantity period) {
        this.period = period;
    }

    public void setPickID(String pickID) {
        this.pickID = pickID;
    }

    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    public void setScalingTime(Time scalingTime) {
        this.scalingTime = scalingTime;
    }

    public void setSnr(Float snr) {
        this.snr = snr;
    }

    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setWaveformID(WaveformStreamID waveformID) {
        this.waveformID = waveformID;
    }

    String publicID;

    String pickID;

    String type;

    RealQuantity genericAmplitude;

    RealQuantity period;

    TimeWindow timeWindow;

    Time scalingTime;

    Float snr;

    String category;

    String unit;

    String methodID;

    WaveformStreamID waveformID;

    String filterID;

    String magnitudeHint;

    String evaluationMode;

    String evaluationStatus;

    CreationInfo creationInfo;

    List<Comment> commentList = new ArrayList<Comment>();
}
