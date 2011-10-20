package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Generic extends AbstractResponseType {

    public Generic(XMLEventReader reader) throws XMLStreamException, NumberFormatException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.COMMENT)) {
                    genComment = StaxUtil.pullText(reader, StationXMLTagNames.GENCOMMENT);
                } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.SENSITIVITY)) {
                    sensitivity = StaxUtil.pullFloat(reader, StationXMLTagNames.SENSITIVITY);
                } else if (elName.equals(StationXMLTagNames.FREEFREQ)) {
                    freeFreq = StaxUtil.pullFloat(reader, StationXMLTagNames.FREEFREQ);
                } else if (elName.equals(StationXMLTagNames.HIGHPASS)) {
                    highPass = StaxUtil.pullFloat(reader, StationXMLTagNames.HIGHPASS);
                } else if (elName.equals(StationXMLTagNames.LOWPASS)) {
                    lowPass = StaxUtil.pullFloat(reader, StationXMLTagNames.LOWPASS);
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
    
    private String genComment;
    private Float sensitivity, freeFreq, highPass, lowPass;
    
    
}
