package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Channel extends BaseNodeType {

    /** for hibernate, etc */
    public Channel() {}
    
    public Channel(Station station) {
        setStation(station);
        setLatitude(station.getLatitude());
        setLongitude(station.getLongitude());
        setElevation(station.getElevation());
        setDepth(0);
    }

    public Channel(Station station, String locCode, String chanCode) {
        this(station);
        this.locCode = locCode;
        this.code = chanCode;
    }

    public Channel(Station station, String locCode, String chanCode, Instant startTime, Instant endTime) {
        this(station);
        this.locCode = locCode;
        this.code = chanCode;
        this.startDateTime = startTime;
        this.endDateTime = endTime;
    }
    
    public Channel(XMLEventReader reader, Station station) throws XMLStreamException,
            StationXMLException {
        this(station);
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.CHANNEL, reader);
        super.parseAttributes(startE);
        locCode = Channel.fixLocCode(StaxUtil.pullAttribute(startE, StationXMLTagNames.LOC_CODE));
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
                } else if (elName.equals(StationXMLTagNames.LAT)) {
                    latitude = new DegreeFloatType(reader, StationXMLTagNames.LAT);
                } else if (elName.equals(StationXMLTagNames.LON)) {
                    longitude = new DegreeFloatType(reader, StationXMLTagNames.LON);
                } else if (elName.equals(StationXMLTagNames.ELEVATION)) {
                    elevation = new MeterFloatType(reader, StationXMLTagNames.ELEVATION);
                } else if (elName.equals(StationXMLTagNames.DEPTH)) {
                    depth = new MeterFloatType(reader, StationXMLTagNames.DEPTH);
                } else if (elName.equals(StationXMLTagNames.AZIMUTH)) {
                    azimuth = new DegreeFloatType(reader, StationXMLTagNames.AZIMUTH);
                } else if (elName.equals(StationXMLTagNames.DIP)) {
                    dip = new DegreeFloatType(reader, StationXMLTagNames.DIP);
                } else if (elName.equals(StationXMLTagNames.TYPE)) {
                    typeList.add(StaxUtil.pullText(reader, StationXMLTagNames.TYPE));
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE)) {
                    sampleRate = new FloatType(reader, StationXMLTagNames.SAMPLE_RATE, Unit.HERTZ);
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE_RATIO)) {
                    sampleRateRatio = new SampleRateRatio(reader);
                } else if (elName.equals(StationXMLTagNames.STORAGEFORMAT)) {
                    storageFormat = StaxUtil.pullText(reader, StationXMLTagNames.STORAGEFORMAT);
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
                    equipmentList.add(new Equipment(reader));
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

    public Equipment getSensor() {
        return sensor;
    }

    public Equipment getPreAmplifier() {
        return preAmplifier;
    }

    public Equipment getDataLogger() {
        return dataLogger;
    }

    public List<Equipment> getEquipment() {
        return equipmentList;
    }

    public Response getResponse() {
        return response;
    }
    
    public InstrumentSensitivity getInstrumentSensitivity() {
        return getResponse() != null ? getResponse().getInstrumentSensitivity() : null;
    }
    
    public void setInstrumentSensitivity(InstrumentSensitivity sensitivity) {
        if (getResponse() == null) {
            this.response = new Response();
        }
        this.response.setInstrumentSensitivity(sensitivity);
    }
    public String getChannelCode() {
        return getCode();
    }
    
    public void setChannelCode(String code) {
        setCode(code);
    }

    public String getLocCode() {
        return locCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getNetworkCode() {
        return getNetwork().getNetworkCode();
    }

    public String getNetworkId() {
        return getNetwork().getNetworkId();
    }

    public DegreeFloatType getLatitude() {
        return latitude;
    }

    public DegreeFloatType getLongitude() {
        return longitude;
    }

    public FloatType getElevation() {
        return elevation;
    }

    public FloatType getDepth() {
        return depth;
    }
    
    public float getLatitudeFloat() {
        return getLatitude().getValue();
    }

    public float getLongitudeFloat() {
        return getLongitude().getValue();
    }

    public float getElevationFloat() {
        return getElevation().getValue();
    }
    
    public float getDepthFloat() {
        return getDepth().getValue();
    }

    public DegreeFloatType getAzimuth() {
        return azimuth;
    }

    public DegreeFloatType getDip() {
        return dip;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    @Deprecated
    public String getStorageFormat() {
        return storageFormat;
    }

    @Override
    public String toString() {
        return getNetworkCode()+"."+getStationCode()+"."+getLocCode()+"."+getCode();
    }

    
    
    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    
    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    
    public Station getStation() {
        return station;
    }
    
    public Network getNetwork() {
        try {
            return getStation().getNetwork();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setStation(Station station) {
        this.station = station;
        this.stationCode = station.getStationCode();
    }

    public void setLongitude(float longitude) {
    		setLongitude(new DegreeFloatType(longitude));
    }
    
    public void setLongitude(DegreeFloatType longitude) {
        this.longitude = longitude;
    }

    
    public void setSampleRateRatio(SampleRateRatio sampleRateRatio) {
        this.sampleRateRatio = sampleRateRatio;
    }

    public void setSampleRate(float sampleRate) {
    	    setSampleRate(new FloatType(sampleRate, Unit.HERTZ));
    }

    public void setSampleRate(FloatType sampleRate) {
        this.sampleRate = sampleRate;
    }


    public void setClockDrift(float clockDrift) {
        setClockDrift(new FloatType(clockDrift, "s/s"));
    }
    
    public void setClockDrift(FloatType clockDrift) {
        this.clockDrift = clockDrift;
    }

    
    public void setClockDriftUnit(String clockDriftUnit) {
        this.clockDriftUnit = clockDriftUnit;
    }

    
    public void setCalibrationUnits(Unit calibrationUnits) {
        this.calibrationUnits = calibrationUnits;
    }

    
    public void setSensor(Equipment sensor) {
        this.sensor = sensor;
    }

    
    public void setPreAmplifier(Equipment preAmplifier) {
        this.preAmplifier = preAmplifier;
    }

    
    public void setDataLogger(Equipment dataLogger) {
        this.dataLogger = dataLogger;
    }

    
    public void appendEquipment(Equipment equip) {
        equipmentList.add(equip);
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    
    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }

    public void setLatitude(float latitude) {
    		setLatitude(new DegreeFloatType(latitude));
    }
    
    public void setLatitude(DegreeFloatType latitude) {
        this.latitude = latitude;
    }

    public void setElevation(float elevation) {
      	setElevation(new FloatType(elevation, Unit.METER));
    }
    
    public void setElevation(FloatType elevation) {
        this.elevation = elevation;
    }

    public void setDepth(float depth) {
    		setDepth(new FloatType(depth, Unit.METER));
    }
    
    public void setDepth(FloatType depth) {
        this.depth = depth;
    }

    public void setAzimuth(float azimuth) {
    	    setAzimuth(new DegreeFloatType(azimuth));
    }
    
    public void setAzimuth(DegreeFloatType azimuth) {
        this.azimuth = azimuth;
    }

    public void setDip(float dip) {
    	    setDip(new DegreeFloatType(dip));
    }
    
    public void setDip(DegreeFloatType dip) {
        this.dip = dip;
    }


    public MeterFloatType getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(MeterFloatType waterlevel) {
        this.waterlevel = waterlevel;
    }
    
    /** set waterlevel in METERS. */
    public void setWaterlevel(float level) {
        setWaterlevel(new MeterFloatType(level));
    }
    
    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    @Deprecated
    public void setStorageFormat(String storageFormat) {
        this.storageFormat = storageFormat;
    }

    public List<ExternalReference> getExternalReferenceList() {
        return externalReferenceList;
    }

    public void setExternalReferenceList(List<ExternalReference> externalReferenceList) {
        this.externalReferenceList = externalReferenceList;
    }

    public void appendExternalReference(ExternalReference extRef) {
        this.externalReferenceList.add(extRef);        
    }

    private Station station;

    private SampleRateRatio sampleRateRatio;

    private FloatType sampleRate;

    private FloatType clockDrift;

    private String clockDriftUnit = "SECONDS/SAMPLE";

    private Unit calibrationUnits;

    private Equipment sensor;

    private Equipment preAmplifier;

    private Equipment dataLogger;

    private List<Equipment> equipmentList = new ArrayList<Equipment>();

    private Response response;

    private String locCode, stationCode;

    private DegreeFloatType latitude, longitude, azimuth, dip;

    private MeterFloatType waterlevel;
    
    private FloatType elevation, depth;

    List<String> typeList = new ArrayList<String>();

    String storageFormat;
    
    List<ExternalReference> externalReferenceList = new ArrayList<ExternalReference>();
    
    public static String fixLocCode(String locCode) {
        String out = locCode;
        if (locCode == null ) { 
            out = EMPTY_LOC_CODE; 
        } else {
            out = out.trim();
            if (out.length() == 0 || out.equals("--")) { out = EMPTY_LOC_CODE; }
        }
        return out;
    }
    
    public static final String EMPTY_LOC_CODE = "";

    public void associateInDb(Channel indb) {
        setDbid(indb.getDbid());
    }

}
