package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class PolesZeros extends AbstractResponseType {

    private String comment;
    private float normalizationFactor;
    private float normalizationFreq;
    private List<Pole> poleList = new ArrayList<Pole>();
    private List<Zero> zeroList = new ArrayList<Zero>();

    public PolesZeros(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.COMMENT)) {
                    comment = StaxUtil.pullText(reader, StationXMLTagNames.COMMENT);
                } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
                    inputUnits = StaxUtil.pullText(reader, StationXMLTagNames.INPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
                    outputUnits = StaxUtil.pullText(reader, StationXMLTagNames.OUTPUTUNITS);
                } else if (elName.equals(StationXMLTagNames.NORMALIZATIONFACTOR)) {
                    normalizationFactor = StaxUtil.pullFloat(reader, StationXMLTagNames.NORMALIZATIONFACTOR);
                } else if (elName.equals(StationXMLTagNames.NORMALIZATIONFREQ)) {
                    normalizationFreq = StaxUtil.pullFloat(reader, StationXMLTagNames.NORMALIZATIONFREQ);
                } else if (elName.equals(StationXMLTagNames.POLE)) {
                    poleList.add( new Pole(reader));
                } else if (elName.equals(StationXMLTagNames.ZERO)) {
                    zeroList.add( new Zero(reader));
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
}
