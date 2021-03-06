package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Site {

	Site() {
		
	}
	
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
    
    public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
    public String toString() {
        return getName();
    }

    String name, description, town, county, region, country;
}
