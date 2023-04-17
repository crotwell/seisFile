package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Station extends BaseNodeType {

    public Station() {}
    
    public Station(Network network, String code) {
        this.setNetwork(network);
        this.code = code;
    }

    public Station(XMLEventReader reader, Network network) throws XMLStreamException, StationXMLException {
        this.network = network;
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.STATION, reader);
        super.parseAttributes(startE);
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
                } else if (elName.equals(StationXMLTagNames.SITE)) {
                    site = new Site(reader);
                } else if (elName.equals(StationXMLTagNames.WATERLEVEL)) {
                    waterlevel = new MeterFloatType(reader, StationXMLTagNames.WATERLEVEL);
                } else if (elName.equals(StationXMLTagNames.VAULT)) {
                    vault = StaxUtil.pullText(reader, StationXMLTagNames.VAULT);
                } else if (elName.equals(StationXMLTagNames.GEOLOGY)) {
                    geology = StaxUtil.pullText(reader, StationXMLTagNames.GEOLOGY);
                } else if (elName.equals(StationXMLTagNames.EQUIPMENT)) {
                    equipmentList.add(new Equipment(reader));
                } else if (elName.equals(StationXMLTagNames.OPERATOR)) {
                    operatorList.add(new Operator(reader));
                } else if (elName.equals(StationXMLTagNames.CREATIONDATE)) {
                    creationDate = StaxUtil.pullText(reader, StationXMLTagNames.CREATIONDATE);
                } else if (elName.equals(StationXMLTagNames.TERMINATIONDATE)) {
                    terminationDate = StaxUtil.pullText(reader, StationXMLTagNames.TERMINATIONDATE);
                } else if (elName.equals(StationXMLTagNames.TOTALNUMCHANNELS)) {
                    totalNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.TOTALNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.SELECTEDNUMCHANNELS)) {
                    selectedNumChannels = StaxUtil.pullInt(reader, StationXMLTagNames.SELECTEDNUMCHANNELS);
                } else if (elName.equals(StationXMLTagNames.EXTERNALREFERENCE)) {
                    externalReferenceList.add(new ExternalReference(reader, StationXMLTagNames.EXTERNALREFERENCE));
                } else if (elName.equals(StationXMLTagNames.CHANNEL)) {
                    channelList.add(new Channel(reader, this));
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
    
    public Network getNetwork() {
        return network;
    }

    public String getCreationDate() {
        return creationDate;
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
    
    public float getLatitudeFloat() {
        return getLatitude().getValue();
    }

    public float getLongitudeFloat() {
        return getLongitude().getValue();
    }

    public float getElevationFloat() {
        return getElevation().getValue();
    }

    public Site getSite() {
        return site;
    }

    public int getTotalNumChannels() {
        return totalNumChannels;
    }

    public int getSelectedNumChannels() {
        return selectedNumChannels;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public String getTerminationDate() {
        return terminationDate;
    }

    public String getVault() {
        return vault;
    }

    public String getGeology() {
        return geology;
    }

    public String getNetworkCode() {
        return getNetwork().getNetworkCode();
    }

    /**
     * Same as getNetworkCode for 2 char permanent networks, but appends the year for temp networks.
     * @return
     */
    public String getNetworkId() {
        return getNetwork().getNetworkId();
    }
    
    public String getStationCode() {
        return getCode();
    }
    
    public void setStationCode(String code) {
        setCode(code);
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public List<Operator> getOperatorList() {
        return operatorList;
    }

    public List<ExternalReference> getExternalReferenceList() {
        return externalReferenceList;
    }

    @Override
    public String toString() {
        return getNetworkCode()+"."+getCode();
    }

    public void setNetwork(Network network) {
        this.network = network;
        this.networkId = network.getNetworkId();
    }
    
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    
    public void setTerminationDate(String terminationDate) {
        this.terminationDate = terminationDate;
    }

    public void setLatitude(float latitude) {
    		setLatitude(new DegreeFloatType(latitude));
    }
    
    public void setLatitude(DegreeFloatType latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
    	    setLongitude(new DegreeFloatType(longitude));
    }
    
    public void setLongitude(DegreeFloatType longitude) {
        this.longitude = longitude;
    }

    /** set elevation in METERS. */
    public void setElevation(float elevation) {
        setElevation(new FloatType(elevation, Unit.METER));
    }
    
    public void setElevation(FloatType elevation) {
        this.elevation = elevation;
    }

    
    public void setVault(String vault) {
        this.vault = vault;
    }

    
    public void setGeology(String geology) {
        this.geology = geology;
    }

    
    public void setSite(Site site) {
        this.site = site;
    }

    
    public void setTotalNumChannels(int totalNumChannels) {
        this.totalNumChannels = totalNumChannels;
    }

    
    public void setSelectedNumChannels(int selectedNumChannels) {
        this.selectedNumChannels = selectedNumChannels;
    }

    
    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    
    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }


    public void appendEquipment(Equipment e) {
        equipmentList.add(e);
    }

    public void setOperatorList(List<Operator> operatorList) {
        this.operatorList = operatorList;
    }
    
    @Deprecated
    public void addOperator(Operator operator) {
        this.operatorList.add(operator);
    }

    
    public void appendOperator(Operator operator) {
        this.operatorList.add(operator);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setExternalReferenceList(List<ExternalReference> externalReferenceList) {
        this.externalReferenceList = externalReferenceList;
    }

    public void appendExternalReference(ExternalReference extRef) {
        this.externalReferenceList.add(extRef);        
    }

    public void associateInDb(Station sta) {
        setDbid(sta.getDbid());
    }
    
    Network network;

    String creationDate, terminationDate;

    DegreeFloatType latitude, longitude;
    
    FloatType elevation;

    String name;

    String vault;

    MeterFloatType waterlevel;

    String geology;

    Site site;

    int totalNumChannels;

    int selectedNumChannels;
    
    String networkId;

    List<Channel> channelList = new ArrayList<Channel>();

    List<Equipment> equipmentList = new ArrayList<Equipment>();
    
    List<Operator> operatorList = new ArrayList<Operator>();

    List<ExternalReference> externalReferenceList = new ArrayList<ExternalReference>();
}
