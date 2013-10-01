package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class WaveformStreamID {
    
    public WaveformStreamID(final XMLEventReader reader, String elementName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        networkCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.networkCode);
        stationCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.stationCode);
        locationCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.locationCode);
        channelCode = StaxUtil.pullAttribute(startE, QuakeMLTagNames.channelCode);
        resourceReference = StaxUtil.pullContiguousText(reader); //eats the end element, ignores any sub elements
    }
    
    String networkCode;
    String stationCode;
    String locationCode;
    String channelCode;
    String resourceReference;
}
