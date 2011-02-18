package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



public class Station {
    
    public Station(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(StaMessage.STATION)) {
            XMLEvent e = reader.nextEvent(); // pop Station
            netCode = StaxUtil.pullAttribute(e.asStartElement(), StaMessage.NET_CODE);
            staCode = StaxUtil.pullAttribute(e.asStartElement(), StaMessage.STA_CODE);
            while(reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals("StationEpoch")) {
                        staList.add(new StationEpoch(reader));
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
        } else {
            throw new StationXMLException("Not a Station element: "+cur.asStartElement().getName().getLocalPart());
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
