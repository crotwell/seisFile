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
    
    public Response(List<ResponseStage> responseStageList,
                    InstrumentSensitivity instrumentSensitivity,
                    InstrumentPolynomial instrumentPolynomial) {
        this.responseStageList = responseStageList;
        this.instrumentSensitivity = instrumentSensitivity;
        this.instrumentPolynomial = instrumentPolynomial;
    }

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
    
    /**
     * @deprecated Use {@link InstrumentSensitivity#isValid(InstrumentSensitivity)} instead
     */
    public static boolean isValid(InstrumentSensitivity sens) {
        return InstrumentSensitivity.isValid(sens);
    }
    
    public static boolean isValid(Response resp) {
        return resp != null && resp.responseStageList.size() != 0 && InstrumentSensitivity.isValid(resp.instrumentSensitivity);
    }
    
    public static void checkResponse(Response resp) throws InvalidResponse {
        if ( ! isValid(resp)) {
            if (resp.responseStageList.size() == 0) {
                throw new InvalidResponse("response is not valid, zero stages");
            } else if (resp.instrumentSensitivity.frequency < 0) {
                throw new InvalidResponse("response is not valid, sensitivity frequency negative");
            } else if (resp.instrumentSensitivity.sensitivityValue == -1) {
                throw new InvalidResponse("response is not valid, sensitivity factor = -1");
            } else {
                ResponseStage stageZero = resp.responseStageList.get(0);
                if (stageZero.getStageSensitivity().sensitivityValue == 1 && 
                        stageZero.getResponseItem() instanceof PolesZeros &&
                        ((PolesZeros)stageZero.getResponseItem()).getPoleList().size() == 0 &&
                                ((PolesZeros)stageZero.getResponseItem()).getZeroList().size() == 0 ) {
                throw new InvalidResponse("response is not valid, stage[0] gain = 1, no poles, no zeros, marker for \"UNKNOWN\"");
                }
            }
            throw new InvalidResponse("Response invalid, reason unknown");
        }
    }


    /**
     * Checks for nonsense sensitivity (overall gain of -1) and trys to repair by multiplying the
     * gains of the individual stages. This only works if all the frequencys are either the same
     * or zero. We assume a frequency of zero means that there is no frequnecy dependence for this
     * stage. 
     */
    public static void repairResponse(Response resp) throws InvalidResponse {
        if(isValid(resp)) {
            return;
        }
        List<ResponseStage> stageList = resp.responseStageList;
        float sensitivity = 1;
        float stageFreq = 0;
        for (ResponseStage responseStage : stageList) {
            if (stageFreq == 0) {stageFreq = responseStage.getStageSensitivity().getFrequency();}
            if (responseStage.getStageSensitivity().getFrequency() != 0 
                    && stageFreq != responseStage.getStageSensitivity().getFrequency()) {
                throw new InvalidResponse("No sensitivity and different frequencies in the stages of the response. Stage 0="
                    +stageFreq+"  stage "+responseStage.getNumber()+"= "+responseStage.getStageSensitivity().getFrequency());
            }
            sensitivity *= responseStage.getStageSensitivity().sensitivityValue;
        }
        resp.instrumentSensitivity.sensitivityValue = sensitivity;
        resp.instrumentSensitivity.frequency = stageFreq;
    }

    public ResponseStage getFirstStage() {
        return getResponseStageList().get(0);
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
