package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Station {

    public Station(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.STATION, reader);
        netCode = StaxUtil.pullAttribute(startE, StationXMLTagNames.NET_CODE);
        staCode = StaxUtil.pullAttribute(startE, StationXMLTagNames.STA_CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.STATION_EPOCH)) {
                    staList.add(new StationEpoch(reader));
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

    List<StationEpoch> staList = new ArrayList<StationEpoch>();

    String netCode, staCode;

    public String getNetCode() {
        return netCode;
    }

    public void setNetCode(String netCode) {
        this.netCode = netCode;
    }

    public String getStaCode() {
        return staCode;
    }

    public void setStaCode(String staCode) {
        this.staCode = staCode;
    }

    public List<StationEpoch> getStationEpochs() {
        return staList;
    }
}
