
package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Response {
    
    private Integer stage;
    private String stageDescription;
    private List<AbstractResponseType> responseItems = new ArrayList<AbstractResponseType>();
    private Decimation decimation;
    private GainSensitivity stageSensitivity;

    public Response(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.RESPONSE, reader);
        stage = StaxUtil.pullIntAttribute(startE, StationXMLTagNames.STAGE);
        try {
            stageDescription = StaxUtil.pullAttribute(startE, StationXMLTagNames.STAGEDESCRIPTION);
        } catch(StationXMLException e) {}
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.POLESZEROS)) {
                    responseItems.add( new PolesZeros(reader));
                } else if (elName.equals(StationXMLTagNames.COEFFICIENTS)) {
                    responseItems.add( new Coefficients(reader));
                } else if (elName.equals(StationXMLTagNames.RESPONSELIST)) {
                    responseItems.add( new ResponseList(reader));
                } else if (elName.equals(StationXMLTagNames.GENERIC)) {
                    responseItems.add( new Generic(reader));
                } else if (elName.equals(StationXMLTagNames.FIR)) {
                    responseItems.add( new FIR(reader));
                } else if (elName.equals(StationXMLTagNames.POLYNOMIAL)) {
                    responseItems.add( new Polynomial(reader));
                } else if (elName.equals(StationXMLTagNames.DECIMATION)) {
                    decimation = new Decimation(reader);
                } else if (elName.equals(StationXMLTagNames.STAGESENSITIVITY)) {
                    stageSensitivity = new GainSensitivity(reader, StationXMLTagNames.STAGESENSITIVITY);
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

    
    public Integer getStage() {
        return stage;
    }

    
    public String getStageDescription() {
        return stageDescription;
    }

    
    public List<AbstractResponseType> getResponseItems() {
        return responseItems;
    }

    
    public Decimation getDecimation() {
        return decimation;
    }

    
    public GainSensitivity getStageSensitivity() {
        return stageSensitivity;
    }

}
