
package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class ResponseStage {
    
    private Integer number;
    private String resourceId;
    private BaseFilterType responseItem;
    private Decimation decimation;
    private GainSensitivity stageGain;

    public ResponseStage(Integer number,
                         String resourceId,
                         BaseFilterType responseItem,
                         Decimation decimation,
                         GainSensitivity stageGain) {
        super();
        this.number = number;
        this.resourceId = resourceId;
        this.responseItem = responseItem;
        this.decimation = decimation;
        this.stageGain = stageGain;
    }


    public ResponseStage(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.RESPONSESTAGE, reader);
        number = StaxUtil.pullIntAttribute(startE, StationXMLTagNames.NUMBER);
        try {
            resourceId = StaxUtil.pullAttribute(startE, StationXMLTagNames.RESOURCEID);
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
                } else if (elName.equals(StationXMLTagNames.FIR)) {
                    responseItem = new FIR(reader);
                } else if (elName.equals(StationXMLTagNames.POLYNOMIAL)) {
                    responseItem = new Polynomial(reader);
                } else if (elName.equals(StationXMLTagNames.DECIMATION)) {
                    decimation = new Decimation(reader);
                } else if (elName.equals(StationXMLTagNames.STAGEGAIN)) {
                    stageGain = new GainSensitivity(reader, StationXMLTagNames.STAGEGAIN);
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

    
    public Integer getNumber() {
        return number;
    }

    
    public String getResourceId() {
        return resourceId;
    }

    
    public BaseFilterType getResponseItem() {
        return responseItem;
    }

    
    public Decimation getDecimation() {
        return decimation;
    }

    
    public GainSensitivity getStageSensitivity() {
        return stageGain;
    }

}
