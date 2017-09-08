package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Network extends BaseNodeType {

    public Network() {
        
    }

    public Network(String code) {
        this.code = code;
    }
    
    public Network(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.NETWORK, reader);
        super.parseAttributes(startE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
                } else if (elName.equals(StationXMLTagNames.TOTALNUMSTATIONS)) {
                        totalNumStations = StaxUtil.pullInt(reader, StationXMLTagNames.TOTALNUMSTATIONS);
                } else if (elName.equals(StationXMLTagNames.SELECTEDNUMSTATIONS)) {
                    selectedNumStations = StaxUtil.pullInt(reader, StationXMLTagNames.SELECTEDNUMSTATIONS);
                } else if (elName.equals(StationXMLTagNames.STATION)) {
                    stations = new StationIterator(reader, this);
                    break;
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

    /** Same as getNetworkCode() for permanent networks, but the start year
     * is appended for temporary networks to make the code unique. 
     * For example for the CO network this returns CO, 
     * but for the XC network that started in 1991 it returns XC1991. 
     * @return
     */
    public String getNetworkId() {
        if (isTemporary()) {
            return getNetworkCode()+getStartDateTime().get(ChronoField.YEAR);
        }
        return getNetworkCode();
    }
    
    public String getNetworkCode() {
        return getCode();
    }
    
    public StationIterator getStations() {
        return stations;
    }
    
    public int getTotalNumStations() {
        return totalNumStations;
    }

    public int getSelectedNumStations() {
        return selectedNumStations;
    }

    
    public void setTotalNumStations(int totalNumStations) {
        this.totalNumStations = totalNumStations;
    }

    
    public void setSelectedNumStations(int selectedNumStations) {
        this.selectedNumStations = selectedNumStations;
    }

    
    public void setStations(StationIterator stations) {
        this.stations = stations;
    }

    @Override
    public String toString() {
        String out = getCode();
        if (isTemporary()) {
            out += getStartYear();
        }
        return out;
    }
    
    public int getStartYear() {
        return getStartDateTime().atZone(TimeUtils.TZ_UTC).getYear();
    }
    
    public String getStartYearString() {
        return ""+getStartYear();
    }
    
    public boolean isTemporary() {
        return tempNetPattern.matcher(getCode()).matches();
    }

    private static Pattern tempNetPattern = Pattern.compile("[1-9XYZ].?");


    int totalNumStations, selectedNumStations;
    
    StationIterator stations = new ListStationIterator(new ArrayList<Station>()); // init to empty
    
}
