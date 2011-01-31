package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class Epoch {
    

    public Epoch(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals("Epoch")) {
            XMLEvent e = reader.nextEvent();
        }
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationEpoch.STARTDATE)) {
                    startDate = StaxUtil.pullText(reader, StationEpoch.STARTDATE);
                } else if (elName.equals(StationEpoch.ENDDATE)) {
                    endDate = StaxUtil.pullText(reader, StationEpoch.ENDDATE);
                } else if (elName.equals(StationEpoch.LAT)) {
                    lat = StaxUtil.pullFloat(reader, StationEpoch.LAT);
                } else if (elName.equals(StationEpoch.LON)) {
                    lon = StaxUtil.pullFloat(reader, StationEpoch.LON);
                } else if (elName.equals(StationEpoch.ELEVATION)) {
                    elevation = StaxUtil.pullFloat(reader, StationEpoch.ELEVATION);
                } else if (elName.equals(DEPTH)) {
                    depth = StaxUtil.pullFloat(reader, DEPTH);
                } else if (elName.equals(AZIMUTH)) {
                    azimuth = StaxUtil.pullFloat(reader, AZIMUTH);
                } else if (elName.equals(DIP)) {
                    dip = StaxUtil.pullFloat(reader, DIP);
                } else if (elName.equals(SAMPLE_RATE)) {
                    sampleRate = StaxUtil.pullFloat(reader, SAMPLE_RATE);
                } else if (elName.equals(SENSOR)) {
                    sensor = new Sensor(reader);
                } else if (elName.equals(INSTRUMENT_SENSITIVITY)) {
                    instrumentSensitivity = new InstrumentSensitivity(reader);
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

    public static final String DEPTH = "Depth";
    public static final String AZIMUTH = "Azimuth";
    public static final String DIP = "Dip";
    public static final String SAMPLE_RATE = "SampleRate";
    public static final String SENSOR = "Sensor";
    public static final String INSTRUMENT_SENSITIVITY = "InstrumentSensitivity";
    

    String startDate, endDate, creationDate;
    float lat, lon, elevation, depth, azimuth, dip, sampleRate;
    Sensor sensor;
    InstrumentSensitivity instrumentSensitivity;
}
