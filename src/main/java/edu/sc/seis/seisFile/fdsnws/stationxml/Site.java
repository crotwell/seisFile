package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class Site {

    public Site(String name, String description, String town, String county, String region, String country) {
        super();
        this.name = name;
        this.description = description;
        this.town = town;
        this.county = county;
        this.region = region;
        this.country = country;
    }

    public Site(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.SITE, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.NAME)) {
                    name = StaxUtil.pullText(reader, StationXMLTagNames.NAME);
                } else if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
                    description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
                } else if (elName.equals(StationXMLTagNames.TOWN)) {
                    town = StaxUtil.pullText(reader, StationXMLTagNames.TOWN);
                } else if (elName.equals(StationXMLTagNames.COUNTY)) {
                    county = StaxUtil.pullText(reader, StationXMLTagNames.COUNTY);
                } else if (elName.equals(StationXMLTagNames.REGION)) {
                    region = StaxUtil.pullText(reader, StationXMLTagNames.REGION);
                } else if (elName.equals(StationXMLTagNames.COUNTRY)) {
                    country = StaxUtil.pullText(reader, StationXMLTagNames.COUNTRY);
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTown() {
        return town;
    }

    public String getCounty() {
        return county;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
    
    public String toSting() {
        return getName()+" "+getDescription()+" "+getTown()+" "+getCounty()+" "+getRegion()+" "+getCountry();
    }

    String name, description, town, county, region, country;
}
