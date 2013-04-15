package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.InstrumentSensitivity;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class Response {

    public Response(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.RESPONSE, reader);
        resourceId = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.RESOURCEID);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.RESPONSESTAGE)) {
                    responseStageList.add(new ResponseStage(reader));
                } else if (elName.equals(StationXMLTagNames.INSTRUMENT_SENSITIVITY)) {
                    instrumentSensitivity = new InstrumentSensitivity(reader);
                } else if (elName.equals(StationXMLTagNames.INSTRUMENT_POLYNOMIAL)) {
                    instrumentPolynomial = new InstrumentPolynomial(reader);
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

    public List<ResponseStage> getResponseStageList() {
        return responseStageList;
    }

    public InstrumentSensitivity getInstrumentSensitivity() {
        return instrumentSensitivity;
    }

    public InstrumentPolynomial getInstrumentPolynomial() {
        return instrumentPolynomial;
    }

    public String getResourceId() {
        return resourceId;
    }

    List<ResponseStage> responseStageList = new ArrayList<ResponseStage>();

    private InstrumentSensitivity instrumentSensitivity;

    private InstrumentPolynomial instrumentPolynomial;

    private String resourceId;
}
