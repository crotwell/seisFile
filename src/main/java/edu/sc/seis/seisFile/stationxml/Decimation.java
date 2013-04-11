package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class Decimation {

    private float inputSampleRate;
    private int factor;
    private int offset;
    private FloatType delay;
    private FloatType correction;

    public Decimation(XMLEventReader reader) throws XMLStreamException, StationXMLException {
            StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.DECIMATION, reader);
            while(reader.hasNext()) {
                XMLEvent e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    if (elName.equals(StationXMLTagNames.INPUTSAMPLERATE)) {
                        inputSampleRate = StaxUtil.pullFloat(reader, StationXMLTagNames.INPUTSAMPLERATE);
                    } else if (elName.equals(StationXMLTagNames.FACTOR)) {
                        factor = StaxUtil.pullInt(reader, StationXMLTagNames.FACTOR);
                    } else if (elName.equals(StationXMLTagNames.OFFSET)) {
                        offset = StaxUtil.pullInt(reader, StationXMLTagNames.OFFSET);
                    } else if (elName.equals(StationXMLTagNames.DELAY)) {
                        delay = new FloatType(reader, StationXMLTagNames.DELAY);
                    } else if (elName.equals(StationXMLTagNames.CORRECTION)) {
                        correction = new FloatType(reader, StationXMLTagNames.CORRECTION);
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

    
    public float getInputSampleRate() {
        return inputSampleRate;
    }

    
    public int getFactor() {
        return factor;
    }

    
    public int getOffset() {
        return offset;
    }

    
    public FloatType getDelay() {
        return delay;
    }

    
    public FloatType getCorrection() {
        return correction;
    }
}