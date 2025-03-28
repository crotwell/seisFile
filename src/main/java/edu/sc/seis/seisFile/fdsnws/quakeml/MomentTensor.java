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
        publicId = StaxUtil.pullAttributeIfExists(startE, QuakeMLTagNames.publicId); // usgs
                                                                                     // doesn't
                                                                                     // have
                                                                                     // publicId
                                                                                     // on
                                                                                     // momentTensors
                                                                                     // yet
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
                } else if (elName.equals(QuakeMLTagNames.varianceReduction)) {
                    varianceReduction = StaxUtil.pullFloat(reader, QuakeMLTagNames.varianceReduction);
                } else if (elName.equals(QuakeMLTagNames.doubleCouple)) {
                    doubleCouple = StaxUtil.pullFloat(reader, QuakeMLTagNames.doubleCouple);
                } else if (elName.equals(QuakeMLTagNames.clvd)) {
                    clvd = StaxUtil.pullFloat(reader, QuakeMLTagNames.clvd);
                } else if (elName.equals(QuakeMLTagNames.sourceTimeFunction)) {
                    sourceTimeFunction = new SourceTimeFunction(reader);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
                } else if (elName.equals(QuakeMLTagNames.category)) {
                    category = StaxUtil.pullText(reader, QuakeMLTagNames.category);
                } else if (elName.equals(QuakeMLTagNames.dataUsed)) {
                    dataUsed = new DataUsed(reader);
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

    public float getClvd() {
        return clvd;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public DataUsed getDataUsed() {
        return dataUsed;
    }

    public String getDerivedOriginID() {
        return derivedOriginID;
    }

    public float getDoubleCouple() {
        return doubleCouple;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getMomentMagnitudeID() {
        return momentMagnitudeID;
    }

    public String getPublicId() {
        return publicId;
    }

    public RealQuantity getScalarMoment() {
        return scalarMoment;
    }

    public Tensor getTensor() {
        return tensor;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClvd(float clvd) {
        this.clvd = clvd;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
    }

    public void setDataUsed(DataUsed dataUsed) {
        this.dataUsed = dataUsed;
    }

    public void setDerivedOriginID(String derivedOriginID) {
        this.derivedOriginID = derivedOriginID;
    }

    public void setDoubleCouple(float doubleCouple) {
        this.doubleCouple = doubleCouple;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public void setMomentMagnitudeID(String momentMagnitudeID) {
        this.momentMagnitudeID = momentMagnitudeID;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setScalarMoment(RealQuantity scalarMoment) {
        this.scalarMoment = scalarMoment;
    }

    
    public void setTensor(Tensor tensor) {
        this.tensor = tensor;
    }

    public SourceTimeFunction getSourceTimeFunction() {
        return sourceTimeFunction;
    }

    public void setSourceTimeFunction(SourceTimeFunction sourceTimeFunction) {
        this.sourceTimeFunction = sourceTimeFunction;
    }

    String publicId;

    String derivedOriginID;

    String momentMagnitudeID;

    SourceTimeFunction sourceTimeFunction;

    RealQuantity scalarMoment;

    Float varianceReduction;

    Tensor tensor;

    float doubleCouple;

    float clvd;

    String methodID;

    private DataUsed dataUsed;

    private String category;

    List<Comment> commentList = new ArrayList<Comment>();

    CreationInfo creationInfo;
}
