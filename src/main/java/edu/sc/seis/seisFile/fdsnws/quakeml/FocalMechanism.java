package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class FocalMechanism {

    public static final String ELEMENT_NAME = QuakeMLTagNames.focalMechanism;

    public FocalMechanism(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.momentTensor)) {
                    momentTensor = new MomentTensor(reader);
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.nodalPlanes)) {
                    StaxUtil.expectStartElement(QuakeMLTagNames.nodalPlanes, reader);
                    // should be NodalPlane inside
                    XMLEvent nodePlaneEl = reader.peek();
                    while (!nodePlaneEl.isStartElement()) {
                        reader.next();
                        nodePlaneEl = reader.peek();
                    }
                    if (nodePlaneEl.isStartElement()
                            && nodePlaneEl.asStartElement()
                                    .getName()
                                    .getLocalPart()
                                    .equals(QuakeMLTagNames.nodalPlane1)) {
                        nodalPlane[0] = new NodalPlane(reader, QuakeMLTagNames.nodalPlane1);
                        // look for second
                        XMLEvent secNodePlaneEl = reader.peek();
                        while (!secNodePlaneEl.isStartElement()) {
                            reader.next();
                            secNodePlaneEl = reader.peek();
                        }
                        if (secNodePlaneEl.isStartElement()
                                && secNodePlaneEl.asStartElement()
                                        .getName()
                                        .getLocalPart()
                                        .equals(QuakeMLTagNames.nodalPlane2)) {
                            nodalPlane[1] = new NodalPlane(reader, QuakeMLTagNames.nodalPlane2);
                        } else {
                            StaxUtil.skipToMatchingEnd(reader);
                        }
                        StaxUtil.skipToStartElement(reader);
                    } else {
                        StaxUtil.skipToMatchingEnd(reader);
                    }
                } else if (elName.equals(QuakeMLTagNames.principalAxes)) {
                    principalAxes = new PrincipalAxes(reader);
                } else if (elName.equals(QuakeMLTagNames.evaluationMode)) {
                    evaluationMode = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationMode);
                } else if (elName.equals(QuakeMLTagNames.evaluationStatus)) {
                    evaluationStatus = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationStatus);
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

    public MomentTensor getMomentTensor() {
        return momentTensor;
    }

    public NodalPlane[] getNodalPlane() {
        return nodalPlane;
    }

    public PrincipalAxes getPrincipalAxes() {
        return principalAxes;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public String getMethodID() {
        return methodID;
    }

    String publicId;

    MomentTensor momentTensor;

    NodalPlane[] nodalPlane = new NodalPlane[2];

    PrincipalAxes principalAxes;

    String evaluationMode;

    String evaluationStatus;

    String methodID;

    List<Comment> commentList = new ArrayList<Comment>();

    CreationInfo creationInfo;
}
