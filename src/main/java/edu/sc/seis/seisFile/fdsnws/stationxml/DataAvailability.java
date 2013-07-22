package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class DataAvailability {

    public DataAvailability(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.DATAAVAILABILITY, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.EXTENT)) {
                    extent = new Extent(reader);
                } else if (elName.equals(StationXMLTagNames.SPAN)) {
                    spanList.add(new Span(reader));
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

    public Extent getExtent() {
        return extent;
    }

    public List<Span> getSpanList() {
        return spanList;
    }

    Extent extent;

    List<Span> spanList;
}
