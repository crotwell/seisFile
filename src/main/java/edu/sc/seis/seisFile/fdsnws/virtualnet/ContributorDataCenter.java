package edu.sc.seis.seisFile.fdsnws.virtualnet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class ContributorDataCenter {

    public ContributorDataCenter(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(VirtualNetTagNames.DATA_CENTER, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(VirtualNetTagNames.NAME)) {
                    name = StaxUtil.pullText(reader, VirtualNetTagNames.NAME);
                } else if (elName.equals(VirtualNetTagNames.URL)) {
                    url = StaxUtil.pullText(reader, VirtualNetTagNames.URL);
                } else {
                    System.err.println("VirtualNetwork skipping " + elName);
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
    
    
    public String getName() {
        return name;
    }
    
    public String getURL() {
        return url;
    }

    String name;
    String url;
}
