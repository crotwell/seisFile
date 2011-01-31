package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class Sensor {


    public Sensor(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(Epoch.SENSOR)) {
            XMLEvent e = reader.nextEvent(); // pop 
            while (reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals(EQUIP_TYPE)) {
                        equipType = StaxUtil.pullText(reader, EQUIP_TYPE);
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
            throw new StationXMLException("Not a Sensor element: " + cur.asStartElement().getName().getLocalPart());
        }
    }
    
    String equipType;
    
    public static final String EQUIP_TYPE = "EquipType";
}
