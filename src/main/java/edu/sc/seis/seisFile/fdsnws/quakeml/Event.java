package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.Comment;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventDescription;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.Pick;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;

public class Event {

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
                e = reader.nextEvent();
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

    private List<EventDescription> descriptionList = new ArrayList<EventDescription>();

    private List<Magnitude> magnitudeList = new ArrayList<Magnitude>();

    private List<StationMagnitude> stationMagnitudeList = new ArrayList<StationMagnitude>();

    private List<Amplitude> amplitudeList = new ArrayList<Amplitude>();

    private List<Comment> commentList = new ArrayList<Comment>();

    private List<Pick> pickList = new ArrayList<Pick>();

    private String type;

    private int irisFECode = -1;

    /*
     * List<StationMagnitude> stationMagnitudeList = new
     * ArrayList<StationMagnitude>();
     */
    private List<Origin> originList = new ArrayList<Origin>();

    private List<FocalMechanism> focalMechanismList = new ArrayList<FocalMechanism>();

    private CreationInfo creationInfo;

    /** For Hibernate/JPA
     */
    private Integer dbid;
}
