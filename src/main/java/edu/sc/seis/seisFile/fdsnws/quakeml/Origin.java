package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.time.ZonedDateTime;
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
import edu.sc.seis.seisFile.fdsnws.stationxml.BaseNodeType;

public class Origin {

	public Origin() {
		
	}
	
	public Origin(ZonedDateTime originTime, float lat, float lon) {
		datetime = originTime;
		latitude = new RealQuantity(lat);
		longitude = new RealQuantity(lon);
	}
	
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

    public String toString() {
        return time.getValue() + " (" + latitude.getValue() + ", " + longitude.getValue() + ") " + depth.getValue();
    }

    public Time getTime() {
        return time;
    }
    
    public ZonedDateTime getDateTime() {
    	 	return BaseNodeType.parseISOString(getTime().value);
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

    public boolean isTimeFixed() {
        return timeFixed;
    }

    public boolean isEpicenterFixed() {
        return epicenterFixed;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getReferenceSystemID() {
        return referenceSystemID;
    }

    public String getRegion() {
        return region;
    }

    
    public Integer getDbid() {
		return dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public void setLatitude(RealQuantity latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(RealQuantity longitude) {
		this.longitude = longitude;
	}

	public void setDepth(RealQuantity depth) {
		this.depth = depth;
	}

	public void setDepthType(String depthType) {
		this.depthType = depthType;
	}

	public void setTimeFixed(boolean timeFixed) {
		this.timeFixed = timeFixed;
	}

	public void setEpicenterFixed(boolean epicenterFixed) {
		this.epicenterFixed = epicenterFixed;
	}

	public void setEarthModelID(String earthModelID) {
		this.earthModelID = earthModelID;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public void setArrivalList(List<Arrival> arrivalList) {
		this.arrivalList = arrivalList;
	}

	public void setWaveformID(String waveformID) {
		this.waveformID = waveformID;
	}

	public void setQuality(OriginQuality quality) {
		this.quality = quality;
	}

	public void setOriginUncertainty(OriginUncertainty originUncertainty) {
		this.originUncertainty = originUncertainty;
	}

	public void setEvaluationMode(String evaluationMode) {
		this.evaluationMode = evaluationMode;
	}

	public void setEvaluationStatus(String evaluationStatus) {
		this.evaluationStatus = evaluationStatus;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCreationInfo(CreationInfo creationInfo) {
		this.creationInfo = creationInfo;
	}

	public void setMethodID(String methodID) {
		this.methodID = methodID;
	}

	public void setReferenceSystemID(String referenceSystemID) {
		this.referenceSystemID = referenceSystemID;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public void setIrisContributor(String irisContributor) {
		this.irisContributor = irisContributor;
	}

	public void setIrisCatalog(String irisCatalog) {
		this.irisCatalog = irisCatalog;
	}

	ZonedDateTime datetime = null;

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
