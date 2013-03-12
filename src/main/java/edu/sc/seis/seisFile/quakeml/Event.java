package edu.sc.seis.seisFile.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StationXMLException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;

public class Event {

    public Event(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.event, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        Attribute feCodeAttr = startE.getAttributeByName(new QName(QuakeMLTagNames.irisNameSpace, QuakeMLTagNames.fecode));
        if (feCodeAttr != null) {
            try {
                irisFECode = Integer.parseInt(feCodeAttr.getValue());
            } catch(NumberFormatException e) {
                throw new SeisFileException("Unable to parse FECode, expected integer but was: "+feCodeAttr.getValue(), e);
            }
        }
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.description)) {
                    descriptionList.add(new EventDescription(reader));
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                    /*
                     * } else if (elName.equals(QuakeMLTagNames.focalMechanism))
                     * { focalMechanismList.add(new FocalMechanism(reader)); }
                     * else if (elName.equals(QuakeMLTagNames.amplitude)) {
                     * amplitudeList.add(new Amplitude(reader)); } else if
                     * (elName.equals(QuakeMLTagNames.magnitude)) {
                     * magnitudeList.add(new Magnitude(reader)); } else if
                     * (elName.equals(QuakeMLTagNames.stationMagnitude)) {
                     * stationMagnitudeList.add(new StationMagnitude(reader));
                     */
                } else if (elName.equals(QuakeMLTagNames.origin)) {
                    originList.add(new Origin(reader));
                } else if (elName.equals(QuakeMLTagNames.magnitude)) {
                    magnitudeList.add(new Magnitude(reader));
                    /*
                     * } else if (elName.equals(QuakeMLTagNames.pick)) {
                     * pickList.add(new Pick(reader));
                     */
                } else if (elName.equals(QuakeMLTagNames.preferredOriginID)) {
                    preferredOriginID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredOriginID);
                } else if (elName.equals(QuakeMLTagNames.preferredMagnitudeID)) {
                    preferredMagnitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredMagnitudeID);
                } else if (elName.equals(QuakeMLTagNames.preferredFocalMechanismID)) {
                    preferredFocalMechanismID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredFocalMechanismID);
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

    public String getPreferredOriginID() {
        return preferredOriginID;
    }

    public String getPreferredMagnitudeID() {
        return preferredMagnitudeID;
    }

    public String getPreferredFocalMechanismID() {
        return preferredFocalMechanismID;
    }

    public List<EventDescription> getDescriptionList() {
        return descriptionList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public List<FocalMechanism> getFocalMechanismList() {
        return focalMechanismList;
    }

    public List<Amplitude> getAmplitudeList() {
        return amplitudeList;
    }

    public List<Origin> getOriginList() {
        return originList;
    }

    public String getPublicId() {
        return publicId;
    }

    public List<Magnitude> getMagnitudeList() {
        return magnitudeList;
    }

    
    public int getIrisFECode() {
        return irisFECode;
    }

    String preferredOriginID, preferredMagnitudeID, preferredFocalMechanismID;

    String publicId;

    List<EventDescription> descriptionList = new ArrayList<EventDescription>();

    List<Magnitude> magnitudeList = new ArrayList<Magnitude>();

    List<Comment> commentList = new ArrayList<Comment>();

    List<FocalMechanism> focalMechanismList = new ArrayList<FocalMechanism>();

    List<Amplitude> amplitudeList = new ArrayList<Amplitude>();
    
    int irisFECode = -1;

    /*
     * List<Magnitude> magnitudeList = new ArrayList<Magnitude>();
     * List<StationMagnitude> stationMagnitudeList = new
     * ArrayList<StationMagnitude>(); List<Pick> pickList = new
     * ArrayList<Pick>();
     */
    List<Origin> originList = new ArrayList<Origin>();
    
}
