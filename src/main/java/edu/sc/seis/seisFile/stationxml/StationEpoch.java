package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class StationEpoch {
    

    public StationEpoch(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals("StationEpoch")) {
            XMLEvent e = reader.nextEvent();
        }
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(STARTDATE)) {
                    startDate = StaxUtil.pullText(reader, STARTDATE);
                } else if (elName.equals(ENDDATE)) {
                    endDate = StaxUtil.pullText(reader, ENDDATE);
                } else if (elName.equals(LAT)) {
                    lat = StaxUtil.pullFloat(reader, LAT);
                } else if (elName.equals(LON)) {
                    lon = StaxUtil.pullFloat(reader, LON);
                } else if (elName.equals(ELEVATION)) {
                    elevation = StaxUtil.pullFloat(reader, ELEVATION);
                } else if (elName.equals(SITE)) {
                    site = new Site(reader);
                } else if (elName.equals(NAME)) {
                    name = StaxUtil.pullText(reader, NAME);
                } else if (elName.equals(CREATIONDATE)) {
                    creationDate = StaxUtil.pullText(reader, CREATIONDATE);
                } else if (elName.equals(NUMCHANNELS)) {
                    numChannels = StaxUtil.pullInt(reader, NUMCHANNELS);
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else  {
                e = reader.nextEvent();
            }
        }
        
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public float getLat() {
        return lat;
    }
    
    public float getLon() {
        return lon;
    }
    
    public float getElevation() {
        return elevation;
    }
    
    public String getName() {
        return name;
    }
    
    public Site getSite() {
        return site;
    }
    
    public int getNumChannels() {
        return numChannels;
    }



    public static final String STARTDATE = "StartDate";
    public static final String ENDDATE = "EndDate";
    public static final String LAT = "Lat";
    public static final String LON = "Lon";
    public static final String ELEVATION = "Elevation";
    public static final String SITE = "Site";
    public static final String NAME = "Name";
    public static final String CREATIONDATE = "CreationDate";
    public static final String NUMCHANNELS = "NumberChannels";

    String startDate, endDate, creationDate;
    float lat, lon, elevation;
    String name;
    Site site;
    int numChannels;
}
