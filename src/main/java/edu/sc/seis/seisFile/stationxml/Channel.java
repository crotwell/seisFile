package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class Channel {

    public Channel(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(StaMessage.CHANNEL)) {
            XMLEvent e = reader.nextEvent(); // pop Station
            locCode = StaxUtil.pullAttribute(e.asStartElement(), StaMessage.LOC_CODE);
            chanCode = StaxUtil.pullAttribute(e.asStartElement(), StaMessage.CHAN_CODE);
            while(reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals(EPOCH)) {
                        chanEpochList.add(new Epoch(reader));
                    } else if (elName.equals(StationEpoch.CREATIONDATE)) {
                        creationDate = StaxUtil.pullText(reader, StationEpoch.CREATIONDATE);
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
            throw new StationXMLException("Not a Channel element: "+cur.asStartElement().getName().getLocalPart());
        }
    }
    
    
    
    
    public List<Epoch> getChanEpochList() {
        return chanEpochList;
    }
    
    public String getChanCode() {
        return chanCode;
    }
    
    public String getLocCode() {
        return locCode;
    }
    
    public String getCreationDate() {
        return creationDate;
    }



    public static final String EPOCH = "Epoch";
    
    List<Epoch> chanEpochList = new ArrayList<Epoch>();
    
    String chanCode, locCode;
    String creationDate;
}
