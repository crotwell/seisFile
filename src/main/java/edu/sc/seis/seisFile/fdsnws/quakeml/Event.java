package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.ISOTimeParser;
import edu.sc.seis.seisFile.LatLonLocatable;
import edu.sc.seis.seisFile.Location;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Event implements LatLonLocatable {

    public static final String ELEMENT_NAME = QuakeMLTagNames.event;

    public Event(Origin origin) {
    	    this.originList.add(origin);
    }
    
    public Event(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.description)) {
                    descriptionList.add(new EventDescription(reader));
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.origin)) {
                    originList.add(new Origin(reader));
                } else if (elName.equals(QuakeMLTagNames.focalMechanism)) {
                    focalMechanismList.add(new FocalMechanism(reader));
                } else if (elName.equals(QuakeMLTagNames.magnitude)) {
                    magnitudeList.add(new Magnitude(reader));
                } else if (elName.equals(QuakeMLTagNames.stationMagnitude)) {
                    stationMagnitudeList.add(new StationMagnitude(reader));
                } else if (elName.equals(QuakeMLTagNames.amplitude)) {
                    amplitudeList.add(new Amplitude(reader));
                } else if (elName.equals(QuakeMLTagNames.pick)) {
                    pickList.add(new Pick(reader));
                } else if (elName.equals(QuakeMLTagNames.preferredOriginID)) {
                    preferredOriginID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredOriginID);
                } else if (elName.equals(QuakeMLTagNames.preferredMagnitudeID)) {
                    preferredMagnitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredMagnitudeID);
                } else if (elName.equals(QuakeMLTagNames.preferredFocalMechanismID)) {
                    preferredFocalMechanismID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredFocalMechanismID);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else {
                    System.err.println("Event skipping " + elName);
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                reader.nextEvent();
            }
        }
    }

    public List<Amplitude> getAmplitudeList() {
        return amplitudeList;
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

    public List<EventDescription> getDescriptionList() {
        return descriptionList;
    }

    public List<FocalMechanism> getFocalMechanismList() {
        return focalMechanismList;
    }

    public int getIrisFECode() {
        return irisFECode;
    }

    public List<Magnitude> getMagnitudeList() {
        return magnitudeList;
    }

    public List<Origin> getOriginList() {
        return originList;
    }

    public List<Pick> getPickList() {
        return pickList;
    }

    public String getPreferredFocalMechanismID() {
        return preferredFocalMechanismID;
    }

    /** Finds the preferred magnitude, returns null if no magnitude
     * matches the preferredMagnitudeID
     * @return preferred magnitude
     */
    public Magnitude getPreferredMagnitude() {
        if (getPreferredMagnitudeID() == null || getPreferredMagnitudeID().isEmpty()) {
            return null;
        }
        for (Magnitude mag : getMagnitudeList()) {
            if (mag.getPublicId().equals(getPreferredMagnitudeID())) {
                return mag;
            }
        }
        return null;
    }

    public String getPreferredMagnitudeID() {
        return preferredMagnitudeID;
    }

    /** Finds the preferred origin, returns null if no origin
     * matches the preferredOriginID
     * @return preferred origin
     */
    public Origin getPreferredOrigin() {
        if (getPreferredOriginID() == null || getPreferredOriginID().isEmpty()) {
            return null;
        }
        for (Origin origin : getOriginList()) {
            if (origin.getPublicId().equals(getPreferredOriginID())) {
                return origin;
            }
        }
        return null;
    }

    public String getPreferredOriginID() {
        return preferredOriginID;
    }

    public String getPublicId() {
        return publicId;
    }

    public List<StationMagnitude> getStationMagnitudeList() {
        return stationMagnitudeList;
    }
    
    public String getType() {
        return type;
    }

	public void setAmplitudeList(List<Amplitude> amplitudeList) {
		this.amplitudeList = amplitudeList;
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

	public void setDescriptionList(List<EventDescription> descriptionList) {
		this.descriptionList = descriptionList;
	}

	public void setFocalMechanismList(List<FocalMechanism> focalMechanismList) {
		this.focalMechanismList = focalMechanismList;
	}

	public void setIrisFECode(int irisFECode) {
		this.irisFECode = irisFECode;
	}

	public void setMagnitudeList(List<Magnitude> magnitudeList) {
		this.magnitudeList = magnitudeList;
	}

	public void setOriginList(List<Origin> originList) {
		this.originList = originList;
	}

	public void setPickList(List<Pick> pickList) {
		this.pickList = pickList;
	}

	public void setPreferredFocalMechanismID(String preferredFocalMechanismID) {
		this.preferredFocalMechanismID = preferredFocalMechanismID;
	}

	public void setPreferredMagnitudeID(String preferredMagnitudeID) {
		this.preferredMagnitudeID = preferredMagnitudeID;
	}

	public void setPreferredOriginID(String preferredOriginID) {
		this.preferredOriginID = preferredOriginID;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public void setStationMagnitudeList(List<StationMagnitude> stationMagnitudeList) {
		this.stationMagnitudeList = stationMagnitudeList;
	}

	public void setType(String type) {
		this.type = type;
	}


	private String preferredOriginID, preferredMagnitudeID, preferredFocalMechanismID;

    private String publicId;

    private List<EventDescription> descriptionList = new ArrayList<>();

    private List<Magnitude> magnitudeList = new ArrayList<>();

    private List<StationMagnitude> stationMagnitudeList = new ArrayList<>();

    private List<Amplitude> amplitudeList = new ArrayList<>();

    private List<Comment> commentList = new ArrayList<>();

    private List<Pick> pickList = new ArrayList<>();

    private String type;

    private int irisFECode = -1;

    /*
     * List<StationMagnitude> stationMagnitudeList = new
     * ArrayList<StationMagnitude>();
     */
    private List<Origin> originList = new ArrayList<>();

    private List<FocalMechanism> focalMechanismList = new ArrayList<>();

    private CreationInfo creationInfo;

    /** For Hibernate/JPA
     */
    private Integer dbid;

    @Override
    public Location asLocation() {
        Location loc = null;
        Origin origin = getPreferredOrigin();
        if (origin != null) {
            loc = origin.asLocation();
        }
        return loc;
    }

    @Override
    public String getLocationDescription() {
        Origin origin = getPreferredOrigin();
        if (origin != null) {
            Location loc = origin.asLocation();
            // also set description
            Magnitude mag = getPreferredMagnitude();
            StringBuilder desc = new StringBuilder();
            desc.append(ISOTimeParser.formatWithTimezone(origin.getTime().asInstant()));
            if (mag != null) {
                if (desc.length() != 0) {
                    desc.append(" ");
                }
                desc.append(mag.getMag().getValue()).append(" ").append(mag.getType());
            }
            if (desc.length() != 0) {
                desc.append(" ");
            }
            desc.append(Location.formatLatLon(loc.getLatitude()).trim())
                    .append("/")
                    .append(Location.formatLatLon(loc.getLongitude()).trim());
            desc.append(" ");
            desc.append(Location.formatLatLon(loc.getDepthKm()).trim());
            desc.append(" km");
            return desc.toString();
        }
        return "";
    }
}
