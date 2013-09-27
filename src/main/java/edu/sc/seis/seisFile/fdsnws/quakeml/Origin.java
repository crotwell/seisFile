package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Origin {

    public static final String ELEMENT_NAME = QuakeMLTagNames.origin;

    public Origin(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        Attribute catalogAttr = startE.getAttributeByName(new QName(QuakeMLTagNames.irisNameSpace,
                                                                    QuakeMLTagNames.irisCatalog));
        if (catalogAttr != null) {
            irisCatalog = catalogAttr.getValue();
        }
        Attribute contributorAttr = startE.getAttributeByName(new QName(QuakeMLTagNames.irisNameSpace,
                                                                        QuakeMLTagNames.irisContributor));
        if (contributorAttr != null) {
            irisContributor = contributorAttr.getValue();
        }
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.waveformID)) {
                    waveformID = StaxUtil.pullText(reader, QuakeMLTagNames.waveformID);
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else if (elName.equals(QuakeMLTagNames.time)) {
                    time = new Time(reader, QuakeMLTagNames.time);
                } else if (elName.equals(QuakeMLTagNames.latitude)) {
                    latitude = new RealQuantity(reader, QuakeMLTagNames.latitude);
                } else if (elName.equals(QuakeMLTagNames.longitude)) {
                    longitude = new RealQuantity(reader, QuakeMLTagNames.longitude);
                } else if (elName.equals(QuakeMLTagNames.depth)) {
                    depth = new RealQuantity(reader, QuakeMLTagNames.depth);
                } else if (elName.equals(QuakeMLTagNames.depthType)) {
                    depthType = StaxUtil.pullText(reader, QuakeMLTagNames.depthType);
                } else if (elName.equals(QuakeMLTagNames.earthModelID)) {
                    earthModelID = StaxUtil.pullText(reader, QuakeMLTagNames.earthModelID);
                } else if (elName.equals(QuakeMLTagNames.quality)) {
                    quality = new OriginQuality(reader);
                } else if (elName.equals(QuakeMLTagNames.originUncertainty)) {
                    originUncertainty = new OriginUncertainty(reader);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.evaluationMode)) {
                    evaluationMode = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationMode);
                } else if (elName.equals(QuakeMLTagNames.evaluationStatus)) {
                    evaluationStatus = StaxUtil.pullText(reader, QuakeMLTagNames.evaluationStatus);
                } else if (elName.equals(QuakeMLTagNames.arrival)) {
                    arrivalList.add(new Arrival(reader));
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

    public String toString() {
        return time.getValue() + " (" + latitude.getValue() + ", " + longitude.getValue() + ") " + depth.getValue();
    }

    public Time getTime() {
        return time;
    }

    public RealQuantity getLatitude() {
        return latitude;
    }

    public RealQuantity getLongitude() {
        return longitude;
    }

    public RealQuantity getDepth() {
        return depth;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public String getWaveformID() {
        return waveformID;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getIrisContributor() {
        return irisContributor;
    }

    public String getIrisCatalog() {
        return irisCatalog;
    }

    public List<Arrival> getArrivalList() {
        return arrivalList;
    }

    public OriginUncertainty getOriginUncertainty() {
        return originUncertainty;
    }

    public String getDepthType() {
        return depthType;
    }

    public String getEarthModelID() {
        return earthModelID;
    }

    public OriginQuality getQuality() {
        return quality;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public String getType() {
        return type;
    }

    Time time;

    RealQuantity latitude;

    RealQuantity longitude;

    RealQuantity depth = new RealQuantity(0.0f); // add default for origins
                                                 // without depth

    String depthType;

    String earthModelID;

    List<Comment> commentList = new ArrayList<Comment>();

    List<Arrival> arrivalList = new ArrayList<Arrival>();

    String waveformID;

    OriginQuality quality;

    OriginUncertainty originUncertainty;

    String evaluationMode;

    String evaluationStatus;

    String type;

    CreationInfo creationInfo;

    String publicId;

    String irisContributor = "";

    String irisCatalog = "";
}
