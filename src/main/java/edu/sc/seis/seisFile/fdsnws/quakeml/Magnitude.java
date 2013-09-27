package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.CreationInfo;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.RealQuantity;

public class Magnitude {

    public static final String ELEMENT_NAME = QuakeMLTagNames.magnitude;

    public Magnitude(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.originID)) {
                    originId = StaxUtil.pullText(reader, QuakeMLTagNames.originID);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.mag)) {
                    mag = new RealQuantity(reader, QuakeMLTagNames.mag);
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

    public String getOriginId() {
        return originId;
    }

    public String getType() {
        return type;
    }

    public RealQuantity getMag() {
        return mag;
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

    String publicId;

    String originId;

    String type;

    RealQuantity mag;

    String evaluationMode;

    String evaluationStatus;

    CreationInfo creationInfo;
}
