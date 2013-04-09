package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Channel extends BaseNodeType {

    private SampleRateRatio sampleRateRatio;

    private float sampleRate;

    private float clockDrift;

    private String clockDriftUnit = "SECONDS/SAMPLE";

    private Unit calibrationUnits;

    private Sensor sensor;

    private PreAmplifier preAmplifier;

    private DataLogger dataLogger;

    private Equipment equipment;

    private Response response;

    public Channel(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.CHANNEL, reader);
        super.parseAttributes(startE);
        locCode = StaxUtil.pullAttribute(startE, StationXMLTagNames.LOC_CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
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
                } else if (elName.equals(StationXMLTagNames.TYPE)) {
                    typeList.add(StaxUtil.pullText(reader, StationXMLTagNames.TYPE));
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE)) {
                    sampleRate = StaxUtil.pullFloat(reader, StationXMLTagNames.SAMPLE_RATE);
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE_RATIO)) {
                    sampleRateRatio = new SampleRateRatio(reader);
                } else if (elName.equals(StationXMLTagNames.CLOCK_DRIFT)) {
                    clockDrift = StaxUtil.pullFloat(reader, StationXMLTagNames.CLOCK_DRIFT);
                    String tmpUnit = StaxUtil.pullAttributeIfExists(e.asStartElement(), StationXMLTagNames.UNIT);
                    if (tmpUnit != null) {
                        clockDriftUnit = tmpUnit;
                    }
                } else if (elName.equals(StationXMLTagNames.CALIBRATIONUNITS)) {
                    calibrationUnits = new Unit(reader);
                } else if (elName.equals(StationXMLTagNames.SENSOR)) {
                    sensor = new Sensor(reader);
                } else if (elName.equals(StationXMLTagNames.PREAMPLIFIER)) {
                    preAmplifier = new PreAmplifier(reader);
                } else if (elName.equals(StationXMLTagNames.DATALOGGER)) {
                    dataLogger = new DataLogger(reader);
                } else if (elName.equals(StationXMLTagNames.EQUIPMENT)) {
                    equipment = new Equipment(reader);
                } else if (elName.equals(StationXMLTagNames.RESPONSE)) {
                    response = new Response(reader);
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

    public SampleRateRatio getSampleRateRatio() {
        return sampleRateRatio;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public float getClockDrift() {
        return clockDrift;
    }

    public Unit getCalibrationUnits() {
        return calibrationUnits;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public PreAmplifier getPreAmplifier() {
        return preAmplifier;
    }

    public DataLogger getDataLogger() {
        return dataLogger;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Response getResponse() {
        return response;
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

    public List<String> getTypeList() {
        return typeList;
    }

    public String getStorageFormat() {
        return storageFormat;
    }

    public String getLocCode() {
        return locCode;
    }

    String locCode;

    float lat, lon, elevation, depth, azimuth, dip;

    List<String> typeList = new ArrayList<String>();

    String storageFormat;
}
