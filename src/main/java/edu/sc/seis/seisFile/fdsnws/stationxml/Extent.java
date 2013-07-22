package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Extent {

    public Extent(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.EXTENT, reader);
        start = StaxUtil.pullAttribute(startE, StationXMLTagNames.START);
        end = StaxUtil.pullAttribute(startE, StationXMLTagNames.END);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                StaxUtil.skipToMatchingEnd(reader);
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    String start;

    String end;
}
