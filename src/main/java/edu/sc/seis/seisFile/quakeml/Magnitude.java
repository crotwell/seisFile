package edu.sc.seis.seisFile.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;

public class Magnitude {

    public Magnitude(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.magnitude, reader);
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

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    String publicId;

    String originId;

    String type;

    RealQuantity mag;

    CreationInfo creationInfo;
}
