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

    public String getOriginId() {
        return originId;
    }

    public RealQuantity getMag() {
        return mag;
    }

    public String getType() {
        return type;
    }

    public String getMethodID() {
        return methodID;
    }

    public WaveformStreamID getWaveformID() {
        return waveformID;
    }

    public String getAmplitudeID() {
        return amplitudeID;
    }

    public String getPublicId() {
        return publicId;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public List<Comment> getCommentList() {
        return commentList;
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
