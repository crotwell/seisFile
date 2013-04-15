package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.stationxml.DataLogger;
import edu.sc.seis.seisFile.fdsnws.stationxml.Equipment;
import edu.sc.seis.seisFile.fdsnws.stationxml.Response;
import edu.sc.seis.seisFile.fdsnws.stationxml.Sensor;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;

public class Channel extends BaseNodeType {

    public Channel(XMLEventReader reader, String networkCode, String stationCode) throws XMLStreamException,
            StationXMLException {
        this.networkCode = networkCode;
        this.stationCode = stationCode;
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
                    latitude = new FloatType(reader, StationXMLTagNames.LAT, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.LON)) {
                    longitude = new FloatType(reader, StationXMLTagNames.LON, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.ELEVATION)) {
                    elevation = new FloatType(reader, StationXMLTagNames.ELEVATION, Unit.METER);
                } else if (elName.equals(StationXMLTagNames.DEPTH)) {
                    depth = new FloatType(reader, StationXMLTagNames.DEPTH, Unit.METER);
                } else if (elName.equals(StationXMLTagNames.AZIMUTH)) {
                    azimuth = new FloatType(reader, StationXMLTagNames.AZIMUTH, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.DIP)) {
                    dip = new FloatType(reader, StationXMLTagNames.DIP, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.TYPE)) {
                    typeList.add(StaxUtil.pullText(reader, StationXMLTagNames.TYPE));
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE)) {
                    sampleRate = new FloatType(reader, StationXMLTagNames.SAMPLE_RATE, Unit.HERTZ);
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE_RATIO)) {
                    sampleRateRatio = new SampleRateRatio(reader);
                } else if (elName.equals(StationXMLTagNames.CLOCK_DRIFT)) {
                    clockDrift = new FloatType(reader, StationXMLTagNames.CLOCK_DRIFT, clockDriftUnit);
                } else if (elName.equals(StationXMLTagNames.CALIBRATIONUNITS)) {
                    calibrationUnits = new Unit(reader, StationXMLTagNames.CALIBRATIONUNITS);
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

    public FloatType getSampleRate() {
        return sampleRate;
    }

    public FloatType getClockDrift() {
        return clockDrift;
    }

    public String getClockDriftUnit() {
        return clockDriftUnit;
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

    public String getLocCode() {
        return locCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public FloatType getLatitude() {
        return latitude;
    }

    public FloatType getLon() {
        return longitude;
    }

    public FloatType getElevation() {
        return elevation;
    }

    public FloatType getDepth() {
        return depth;
    }

    public FloatType getAzimuth() {
        return azimuth;
    }

    public FloatType getDip() {
        return dip;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public String getStorageFormat() {
        return storageFormat;
    }

    private SampleRateRatio sampleRateRatio;

    private FloatType sampleRate;

    private FloatType clockDrift;

    private String clockDriftUnit = "SECONDS/SAMPLE";

    private Unit calibrationUnits;

    private Sensor sensor;

    private PreAmplifier preAmplifier;

    private DataLogger dataLogger;

    private Equipment equipment;

    private Response response;

    private String locCode, stationCode, networkCode;

    private FloatType latitude, longitude, elevation, depth, azimuth, dip;

    List<String> typeList = new ArrayList<String>();

    String storageFormat;
}
