package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class StationMagnitude {

    public static final String ELEMENT_NAME = QuakeMLTagNames.stationMagnitude;

    public StationMagnitude(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.originID)) {
                    originId = StaxUtil.pullText(reader, QuakeMLTagNames.originID);
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.amplitudeID)) {
                    amplitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.amplitudeID);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.mag)) {
                    mag = new RealQuantity(reader, QuakeMLTagNames.mag);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
                } else if (elName.equals(QuakeMLTagNames.waveformID)) {
                    waveformID = new WaveformStreamID(reader, QuakeMLTagNames.waveformID);
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

    public String getAmplitudeID() {
        return amplitudeID;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
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

    public String getType() {
        return type;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public void setAmplitudeID(String amplitudeID) {
        this.amplitudeID = amplitudeID;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setWaveformID(WaveformStreamID waveformID) {
        this.waveformID = waveformID;
    }

    private String originId;

    private RealQuantity mag;

    private String type;

    private String methodID;

    private WaveformStreamID waveformID;

    private String amplitudeID;

    private String publicId;

    CreationInfo creationInfo;

    List<Comment> commentList = new ArrayList<Comment>();
}
