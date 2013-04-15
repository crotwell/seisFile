package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.ListStationIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class Network extends BaseNodeType {

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
                    stations = new StationIterator(reader, getCode());
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

    public StationIterator getStations() {
        return stations;
    }
    
    public int getTotalNumStations() {
        return totalNumStations;
    }

    public int getSelectedNumStations() {
        return selectedNumStations;
    }


    int totalNumStations, selectedNumStations;
    
    StationIterator stations = new ListStationIterator(new ArrayList<Station>()); // init to empty
}
