
package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Response {
    
    private Integer stage;
    private String stageDescription;
    private AbstractResponseType responseItem;
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
                    responseItem = new PolesZeros(reader);
                } else if (elName.equals(StationXMLTagNames.COEFFICIENTS)) {
                    responseItem = new Coefficients(reader);
                } else if (elName.equals(StationXMLTagNames.RESPONSELIST)) {
                    responseItem = new ResponseList(reader);
                } else if (elName.equals(StationXMLTagNames.GENERIC)) {
                    responseItem = new Generic(reader);
                } else if (elName.equals(StationXMLTagNames.FIR)) {
                    responseItem = new FIR(reader);
                } else if (elName.equals(StationXMLTagNames.POLYNOMIAL)) {
                    responseItem = new Polynomial(reader);
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

    
    public AbstractResponseType getResponseItem() {
        return responseItem;
    }

    
    public Decimation getDecimation() {
        return decimation;
    }

    
    public GainSensitivity getStageSensitivity() {
        return stageSensitivity;
    }

}
