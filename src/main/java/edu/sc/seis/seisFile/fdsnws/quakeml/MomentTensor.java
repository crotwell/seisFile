package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class MomentTensor {

    public static final String ELEMENT_NAME = QuakeMLTagNames.momentTensor;

    public MomentTensor(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.derivedOriginID)) {
                    derivedOriginID = StaxUtil.pullText(reader, QuakeMLTagNames.derivedOriginID);
                } else if (elName.equals(QuakeMLTagNames.momentMagnitudeID)) {
                    momentMagnitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.momentMagnitudeID);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.scalarMoment)) {
                    scalarMoment = new RealQuantity(reader, QuakeMLTagNames.scalarMoment);
                } else if (elName.equals(QuakeMLTagNames.tensor)) {
                    tensor = new Tensor(reader);
                } else if (elName.equals(QuakeMLTagNames.doubleCouple)) {
                    doubleCouple = StaxUtil.pullFloat(reader, QuakeMLTagNames.doubleCouple);
                } else if (elName.equals(QuakeMLTagNames.clvd)) {
                    clvd = StaxUtil.pullFloat(reader, QuakeMLTagNames.clvd);
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

    public String getDerivedOriginID() {
        return derivedOriginID;
    }

    public String getMomentMagnitudeID() {
        return momentMagnitudeID;
    }

    public RealQuantity getScalarMoment() {
        return scalarMoment;
    }

    public Tensor getTensor() {
        return tensor;
    }

    public float getDoubleCouple() {
        return doubleCouple;
    }

    public float getClvd() {
        return clvd;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    String publicId;

    String derivedOriginID;

    String momentMagnitudeID;

    RealQuantity scalarMoment;

    Tensor tensor;

    float doubleCouple;

    float clvd;

    List<Comment> commentList = new ArrayList<Comment>();

    CreationInfo creationInfo;
}
