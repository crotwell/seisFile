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

    public String getPublicID() {
        return publicID;
    }

    public String getPickID() {
        return pickID;
    }

    public String getType() {
        return type;
    }

    public RealQuantity getGenericAmplitude() {
        return genericAmplitude;
    }

    public RealQuantity getPeriod() {
        return period;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public Time getScalingTime() {
        return scalingTime;
    }

    public Float getSnr() {
        return snr;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public String getMethodID() {
        return methodID;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public String getFilterID() {
        return filterID;
    }

    public String getMagnitudeHint() {
        return magnitudeHint;
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
