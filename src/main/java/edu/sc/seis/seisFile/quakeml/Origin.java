package edu.sc.seis.seisFile.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StationXMLException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;

public class Origin {

    public Origin(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.origin, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        irisCatalog = StaxUtil.pullAttribute(startE, QuakeMLTagNames.irisCatalog);
        irisContributor = StaxUtil.pullAttribute(startE, QuakeMLTagNames.irisContributor);
        depth = new RealQuantity(0.0f); // add default for origins without depth
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
                    time = new Time(reader);
                } else if (elName.equals(QuakeMLTagNames.latitude)) {
                    latitude = new RealQuantity(reader, QuakeMLTagNames.latitude);
                } else if (elName.equals(QuakeMLTagNames.longitude)) {
                    longitude = new RealQuantity(reader, QuakeMLTagNames.longitude);
                } else if (elName.equals(QuakeMLTagNames.depth)) {
                    depth = new RealQuantity(reader, QuakeMLTagNames.depth);
                } else {
                    System.out.println("Origin skip: "+elName);
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

    private void skipToValue(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        reader.nextEvent();
        StaxUtil.skipToStartElement(reader);
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

    Time time;

    RealQuantity latitude;

    RealQuantity longitude;

    RealQuantity depth;

    List<Comment> commentList = new ArrayList<Comment>();

    String waveformID;

    CreationInfo creationInfo;

    String publicId;

    String irisContributor;

    String irisCatalog;
}
