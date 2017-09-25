package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Unit {

    public static final String SECOND = "second";
    public static final String DEGREE = "degree";
    public static final String METER = "meter";
    public static final String HERTZ = "hertz";
    

    public static final Unit SECOND_UNIT = new Unit(SECOND);
    public static final Unit DEGREE_UNIT = new Unit(DEGREE);
    public static final Unit METER_UNIT = new Unit(METER);
    public static final Unit HERTZ_UNIT = new Unit(HERTZ);

    Unit() {}
    
    public Unit(String name) {
        this(name, null);
    }
    
    public Unit(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Unit(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        this(reader, StationXMLTagNames.UNIT);
    }

    public Unit(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.NAME)) {
                    name = StaxUtil.pullText(reader, StationXMLTagNames.NAME);
                } else if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
                    description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
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
    
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return name;
    }

    String name;
    String description;
}
