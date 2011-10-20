package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Epoch {
    

    public Epoch(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.EPOCH, reader);
        while(reader.hasNext()) {
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
                } else if (elName.equals(StationXMLTagNames.DEPTH)) {
                    depth = StaxUtil.pullFloat(reader, StationXMLTagNames.DEPTH);
                } else if (elName.equals(StationXMLTagNames.AZIMUTH)) {
                    azimuth = StaxUtil.pullFloat(reader, StationXMLTagNames.AZIMUTH);
                } else if (elName.equals(StationXMLTagNames.DIP)) {
                    dip = StaxUtil.pullFloat(reader, StationXMLTagNames.DIP);
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE)) {
                    sampleRate = StaxUtil.pullFloat(reader, StationXMLTagNames.SAMPLE_RATE);
                } else if (elName.equals(StationXMLTagNames.CLOCK_DRIFT)) {
                    clockDrift = StaxUtil.pullFloat(reader, StationXMLTagNames.CLOCK_DRIFT);
                } else if (elName.equals(StationXMLTagNames.SENSOR)) {
                    sensor = new Sensor(reader);
                } else if (elName.equals(StationXMLTagNames.INSTRUMENT_SENSITIVITY)) {
                    instrumentSensitivity = new InstrumentSensitivity(reader);
                } else if (elName.equals(StationXMLTagNames.RESPONSE)) {
                    responseList.add(new Response(reader));
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
    
    public float getDepth() {
        return depth;
    }
    
    public float getAzimuth() {
        return azimuth;
    }
    
    public float getDip() {
        return dip;
    }
    
    public float getSampleRate() {
        return sampleRate;
    }
    
    public float getClockDrift() {
        return clockDrift;
    }
    
    public Sensor getSensor() {
        return sensor;
    }
    
    public InstrumentSensitivity getInstrumentSensitivity() {
        return instrumentSensitivity;
    }

    public List<Response> getResponseList() {
        return responseList;
    }
    
    String startDate, endDate, creationDate;
    float lat, lon, elevation, depth, azimuth, dip, sampleRate, clockDrift;
    

    Sensor sensor;
    InstrumentSensitivity instrumentSensitivity;
    List<Response> responseList = new ArrayList<Response>();
    
}
