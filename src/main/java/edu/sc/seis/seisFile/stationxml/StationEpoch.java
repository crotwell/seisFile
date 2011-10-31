package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class StationEpoch {
    

    public StationEpoch(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.STATION_EPOCH, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.STARTDATE)) {
                    startDate = StaxUtil.pullText(reader, StationXMLTagNames.STARTDATE);
                } else if (elName.equals(StationXMLTagNames.ENDDATE)) {
                    endDate = StaxUtil.pullText(reader, StationXMLTagNames.ENDDATE);
                } else if (elName.equals(StationXMLTagNames.LAT)) {
                    lat = StaxUtil.pullFloat(reader, StationXMLTagNames.LAT);
                } else if (elName.equals(StationXMLTagNames.LON)) {
                    lon = StaxUtil.pullFloat(reader, StationXMLTagNames.LON);
                } else if (elName.equals(StationXMLTagNames.ELEVATION)) {
                    elevation = StaxUtil.pullFloat(reader, StationXMLTagNames.ELEVATION);
                } else if (elName.equals(StationXMLTagNames.SITE)) {
                    site = new Site(reader);
                } else if (elName.equals(StationXMLTagNames.NAME)) {
                    name = StaxUtil.pullText(reader, StationXMLTagNames.NAME);
                } else if (elName.equals(StationXMLTagNames.CREATIONDATE)) {
                    creationDate = StaxUtil.pullText(reader, StationXMLTagNames.CREATIONDATE);
                } else if (elName.equals(StationXMLTagNames.TOTALNUMCHANNELS)) {
                    totalNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.TOTALNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.SELECTEDNUMCHANNELS)) {
                    selectedNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.SELECTEDNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.CHANNEL)) {
                    channelList.add(new Channel(reader));
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
    
    public int getTotalNumChannels() {
        return totalNumChannels;
    }
    
    public int getSelectedNumChannels() {
        return selectedNumChannels;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    String startDate, endDate, creationDate;
    float lat, lon, elevation;
    String name;
    Site site;
    int totalNumChannels;
    int selectedNumChannels;
    List<Channel> channelList = new ArrayList<Channel>();
}
