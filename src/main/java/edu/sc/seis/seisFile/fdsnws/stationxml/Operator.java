package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Operator {
    
    public Operator(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.OPERATOR, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.AGENCY)) {
                    agencyList.add(StaxUtil.pullText(reader, StationXMLTagNames.AGENCY));
                } else if (elName.equals(StationXMLTagNames.CONTACT)) {
                    contactList.add(new Person(reader, StationXMLTagNames.CONTACT));
                } else if (elName.equals(StationXMLTagNames.WEBSITE)) {
                    website = StaxUtil.pullText(reader, StationXMLTagNames.WEBSITE);
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
    

    List<String> agencyList = new ArrayList<String>();
    List<Person> contactList = new ArrayList<Person>();
    String website;
}
