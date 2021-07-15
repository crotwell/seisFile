package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class Operator {
    
    public Operator(String operName) {
        this.agencyList.add(operName);
    }
    
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
    
    @Deprecated
    public List<String> getAgencyList() {
        return agencyList;
    }
    
    public String getAgency() {
        return agencyList.get(0);
    }
    
    public List<Person> getContactList() {
        return contactList;
    }
    
    public String getWebsite() {
        return website;
    }



    public void setAgency(String agency) {
        this.agencyList.clear();
        this.agencyList.add(agency);
    }

    public void appendContact(Person contact) {
        this.contactList.add(contact);
    }

    public void setWebsite(String website) {
        this.website = website;
    }



    List<String> agencyList = new ArrayList<String>();
    List<Person> contactList = new ArrayList<Person>();
    String website;
}
