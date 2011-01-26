package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;



public class Station {
    
    public Station(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(StaMessage.STATION)) {
            XMLEvent e = reader.nextEvent(); // pop Station
            Iterator it = e.asStartElement().getAttributes();
            while(it.hasNext()) {
                Attribute a = (Attribute)it.next();
                if (a.getName().getLocalPart().equals(StaMessage.NET_CODE)) {
                    netCode = a.getValue();
                } else if (a.getName().getLocalPart().equals(StaMessage.STA_CODE)) {
                    staCode = a.getValue();
                }
            }
            while(reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    System.out.println("Station <"+elName+">");
                    if (elName.equals("StationEpoch")) {
                        staList.add(new StationEpoch(reader));
                    } else {
                        StaxUtil.skipToMatchingEnd(reader);
                    }
                } else if (e.isEndElement()) {
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

    
    public List<StationEpoch> getStaList() {
        return staList;
    }
}
