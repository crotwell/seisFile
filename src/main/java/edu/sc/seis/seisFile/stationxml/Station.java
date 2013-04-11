package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Station extends BaseNodeType {

    public Station(XMLEventReader reader, String networkCode) throws XMLStreamException, StationXMLException {
        this.networkCode = networkCode;
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.STATION, reader);
        super.parseAttributes(startE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
                } else if (elName.equals(StationXMLTagNames.LAT)) {
                    lat = new FloatType(reader, StationXMLTagNames.LAT, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.LON)) {
                    lon = new FloatType(reader, StationXMLTagNames.LON, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.ELEVATION)) {
                    elevation = new FloatType(reader, StationXMLTagNames.ELEVATION, Unit.METER);
                } else if (elName.equals(StationXMLTagNames.SITE)) {
                    site = new Site(reader);
                } else if (elName.equals(StationXMLTagNames.VAULT)) {
                    vault = StaxUtil.pullText(reader, StationXMLTagNames.VAULT);
                } else if (elName.equals(StationXMLTagNames.GEOLOGY)) {
                    geology = StaxUtil.pullText(reader, StationXMLTagNames.GEOLOGY);
                } else if (elName.equals(StationXMLTagNames.EQUIPMENT)) {
                    equipmentList.add(new Equipment(reader));
                } else if (elName.equals(StationXMLTagNames.CREATIONDATE)) {
                    creationDate = StaxUtil.pullText(reader, StationXMLTagNames.CREATIONDATE);
                } else if (elName.equals(StationXMLTagNames.TERMINATIONDATE)) {
                    terminationDate = StaxUtil.pullText(reader, StationXMLTagNames.TERMINATIONDATE);
                } else if (elName.equals(StationXMLTagNames.TOTALNUMCHANNELS)) {
                    totalNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.TOTALNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.SELECTEDNUMCHANNELS)) {
                    selectedNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.SELECTEDNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.EXTERNALREFERENCE)) {
                    externalReferenceList.add(StaxUtil.pullText(reader, StationXMLTagNames.EXTERNALREFERENCE));
                } else if (elName.equals(StationXMLTagNames.CHANNEL)) {
                    channelList.add(new Channel(reader, networkCode, getCode()));
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

    public String getCreationDate() {
        return creationDate;
    }

    public FloatType getLat() {
        return lat;
    }

    public FloatType getLon() {
        return lon;
    }

    public FloatType getElevation() {
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

    public String getTerminationDate() {
        return terminationDate;
    }

    public String getVault() {
        return vault;
    }

    public String getGeology() {
        return geology;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public List<String> getExternalReferenceList() {
        return externalReferenceList;
    }

    String creationDate, terminationDate;

    FloatType lat, lon, elevation;

    String name;

    String vault;

    String geology;

    Site site;

    int totalNumChannels;

    int selectedNumChannels;

    String networkCode;

    List<Channel> channelList = new ArrayList<Channel>();

    List<Equipment> equipmentList = new ArrayList<Equipment>();

    List<String> externalReferenceList = new ArrayList<String>();
}
