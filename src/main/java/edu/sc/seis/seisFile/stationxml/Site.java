package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Site {

    public Site(String town, String county, String state, String country) {
        super();
        this.town = town;
        this.county = county;
        this.state = state;
        this.country = country;
    }

    public Site(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(StaMessage.SITE)) {
            XMLEvent e = reader.nextEvent(); // pop Site
            while (reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals(TOWN)) {
                        town = StaxUtil.pullText(reader, TOWN);
                    } else if (elName.equals(COUNTY)) {
                        county = StaxUtil.pullText(reader, COUNTY);
                    } else if (elName.equals(STATE)) {
                        state = StaxUtil.pullText(reader, STATE);
                    } else if (elName.equals(COUNTRY)) {
                        country = StaxUtil.pullText(reader, COUNTRY);
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
        } else {
            throw new StationXMLException("Not a Site element: " + cur.asStartElement().getName().getLocalPart());
        }
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String town, county, state, country;

    public static final String TOWN = "Town";

    public static final String COUNTY = "County";

    public static final String STATE = "State";

    public static final String COUNTRY = "Country";
}
