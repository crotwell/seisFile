package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class WaveformStreamID {
    
    public WaveformStreamID(final XMLEventReader reader, String elementName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        networkCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.networkCode);
        stationCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.stationCode);
        locationCode = StaxUtil.pullAttributeIfExists(startE, QuakeMLTagNames.locationCode);
        channelCode = StaxUtil.pullAttributeIfExists(startE, QuakeMLTagNames.channelCode);
        if (channelCode != null && locationCode == null) {
            // no chan means "station pick"? but chan without loc means empty, space space style loc code
            locationCode = "";
        }
        resourceReference = StaxUtil.pullContiguousText(reader); //eats the end element, ignores any sub elements
    }
    
    public String getChannelCode() {
        return channelCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getResourceReference() {
        return resourceReference;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }
    public void setResourceReference(String resourceReference) {
        this.resourceReference = resourceReference;
    }
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    String networkCode;
    String stationCode;
    String locationCode;
    String channelCode;
    String resourceReference;
}
