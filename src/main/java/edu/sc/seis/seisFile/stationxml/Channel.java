package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Channel {

    public Channel(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.CHANNEL, reader);
        locCode = StaxUtil.pullAttribute(startE, StationXMLTagNames.LOC_CODE);
        chanCode = StaxUtil.pullAttribute(startE, StationXMLTagNames.CHAN_CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.EPOCH)) {
                    chanEpochList.add(new Epoch(reader));
                } else if (elName.equals(StationXMLTagNames.CREATIONDATE)) {
                    creationDate = StaxUtil.pullText(reader, StationXMLTagNames.CREATIONDATE);
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

    public List<Epoch> getChanEpochList() {
        return chanEpochList;
    }

    public String getChanCode() {
        return chanCode;
    }

    public String getLocCode() {
        return locCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    List<Epoch> chanEpochList = new ArrayList<Epoch>();

    String chanCode, locCode;

    String creationDate;
}
