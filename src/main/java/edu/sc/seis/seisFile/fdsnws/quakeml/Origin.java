package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.Location;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Origin {

	public static final String ELEMENT_NAME = QuakeMLTagNames.origin;
	
	public Origin() {
		
	}

    public Origin(Instant originTime, float lat, float lon) {
        time = new Time(originTime);
		latitude = new RealQuantity(lat);
		longitude = new RealQuantity(lon);
	}

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
                } else if (elName.equals(QuakeMLTagNames.timeFixed)) {
                    timeFixed = Boolean.parseBoolean(StaxUtil.pullText(reader, QuakeMLTagNames.timeFixed));
                } else if (elName.equals(QuakeMLTagNames.epicenterFixed)) {
                    epicenterFixed = Boolean.parseBoolean(StaxUtil.pullText(reader, QuakeMLTagNames.epicenterFixed));
                } else if (elName.equals(QuakeMLTagNames.earthModelID)) {
                    earthModelID = StaxUtil.pullText(reader, QuakeMLTagNames.earthModelID);
                } else if (elName.equals(QuakeMLTagNames.quality)) {
                    quality = new OriginQuality(reader);
                } else if (elName.equals(QuakeMLTagNames.originUncertainty)) {
                    originUncertainty = new OriginUncertainty(reader);
                } else if (elName.equals(QuakeMLTagNames.methodID)) {
                    methodID = StaxUtil.pullText(reader, QuakeMLTagNames.methodID);
                } else if (elName.equals(QuakeMLTagNames.referenceSystemID)) {
                    referenceSystemID = StaxUtil.pullText(reader, QuakeMLTagNames.referenceSystemID);
                } else if (elName.equals(QuakeMLTagNames.region)) {
                    region = StaxUtil.pullText(reader, QuakeMLTagNames.region);
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
	
    public List<Arrival> getArrivalList() {
        return arrivalList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public Integer getDbid() {
		return dbid;
	}

    public RealQuantity getDepth() {
        return depth;
    }

    public String getDepthType() {
        return depthType;
    }

    public String getEarthModelID() {
        return earthModelID;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public String getIrisCatalog() {
        return irisCatalog;
    }

    public String getIrisContributor() {
        return irisContributor;
    }

    public RealQuantity getLatitude() {
        return latitude;
    }

    public RealQuantity getLongitude() {
        return longitude;
    }

    public Location asLocation() {
        return new Location(
                getLatitude().getValue().floatValue(),
                getLongitude().getValue().floatValue(),
                getDepth().getValue().floatValue());
    }

    public String getMethodID() {
        return methodID;
    }

    public OriginUncertainty getOriginUncertainty() {
        return originUncertainty;
    }

    public String getPublicId() {
        return publicId;
    }

    public OriginQuality getQuality() {
        return quality;
    }

    public String getReferenceSystemID() {
        return referenceSystemID;
    }

    public String getRegion() {
        return region;
    }

    public Time getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getWaveformID() {
        return waveformID;
    }

    public boolean isEpicenterFixed() {
        return epicenterFixed;
    }

    public boolean isTimeFixed() {
        return timeFixed;
    }

    public void setArrivalList(List<Arrival> arrivalList) {
		this.arrivalList = arrivalList;
	}

    
    public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public void setCreationInfo(CreationInfo creationInfo) {
		this.creationInfo = creationInfo;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public void setDepth(RealQuantity depth) {
		this.depth = depth;
	}

	public void setDepthType(String depthType) {
		this.depthType = depthType;
	}

	public void setEarthModelID(String earthModelID) {
		this.earthModelID = earthModelID;
	}

	public void setEpicenterFixed(boolean epicenterFixed) {
		this.epicenterFixed = epicenterFixed;
	}

	public void setEvaluationMode(String evaluationMode) {
		this.evaluationMode = evaluationMode;
	}

	public void setEvaluationStatus(String evaluationStatus) {
		this.evaluationStatus = evaluationStatus;
	}

	public void setIrisCatalog(String irisCatalog) {
		this.irisCatalog = irisCatalog;
	}

	public void setIrisContributor(String irisContributor) {
		this.irisContributor = irisContributor;
	}

	public void setLatitude(RealQuantity latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(RealQuantity longitude) {
		this.longitude = longitude;
	}

	public void setMethodID(String methodID) {
		this.methodID = methodID;
	}

	public void setOriginUncertainty(OriginUncertainty originUncertainty) {
		this.originUncertainty = originUncertainty;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public void setQuality(OriginQuality quality) {
		this.quality = quality;
	}

	public void setReferenceSystemID(String referenceSystemID) {
		this.referenceSystemID = referenceSystemID;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public void setTimeFixed(boolean timeFixed) {
		this.timeFixed = timeFixed;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWaveformID(String waveformID) {
		this.waveformID = waveformID;
	}

	public String toString() {
        return time.getValue() + " (" + latitude.getValue() + ", " + longitude.getValue() + ") " + depth.getValue();
    }

	Time time;

    RealQuantity latitude;

    RealQuantity longitude;

    RealQuantity depth = new RealQuantity(0.0f); // add default for origins
                                                 // without depth

    String depthType;

    boolean timeFixed = false;

    boolean epicenterFixed = false;

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

    String methodID;

    String referenceSystemID;

    String region;

    String publicId;

    String irisContributor = "";

    String irisCatalog = "";
    
    /** For Hibernate/JPA
     */
    private Integer dbid;
}
