package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Person {
    

    public Person(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.NAME)) {
                    name = StaxUtil.pullText(reader, StationXMLTagNames.NAME);
                } else if (elName.equals(StationXMLTagNames.AGENCY)) {
                    agencyList.add(StaxUtil.pullText(reader, StationXMLTagNames.AGENCY));
                } else if (elName.equals(StationXMLTagNames.EMAIL)) {
                    emailList.add(StaxUtil.pullText(reader, StationXMLTagNames.EMAIL));
                } else if (elName.equals(StationXMLTagNames.PHONE)) {
                    phoneList.add(StaxUtil.pullText(reader, StationXMLTagNames.PHONE));
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
    
    String name; // schema allows unbounded names, seems weird.
    List<String> agencyList = new ArrayList<String>();
    List<String> emailList = new ArrayList<String>();
    List<String> phoneList = new ArrayList<String>();
}
