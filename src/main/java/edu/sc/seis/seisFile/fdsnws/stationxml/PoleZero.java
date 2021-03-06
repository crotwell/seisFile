package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.sac.Complex;


public abstract class PoleZero extends Complex {
    

    public PoleZero(FloatNoUnitType realWithError, FloatNoUnitType imaginaryWithError) {
        super(realWithError.value, imaginaryWithError.value);
        this.realWithError = realWithError;
        this.imaginaryWithError = imaginaryWithError;
    }

    public PoleZero(XMLEventReader reader, String elementName) throws XMLStreamException, StationXMLException {
        super(0,0); // dummy values
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.REAL)) {
                    realWithError = new FloatNoUnitType(reader, StationXMLTagNames.REAL);
                    real = realWithError.value;
                } else if (elName.equals(StationXMLTagNames.IMAGINARY)) {
                    imaginaryWithError = new FloatNoUnitType(reader, StationXMLTagNames.IMAGINARY);
                    imaginary = imaginaryWithError.value;
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
    
    public FloatNoUnitType getRealWithError() {
        return realWithError;
    }
    
    public FloatNoUnitType getImaginaryWithError() {
        return imaginaryWithError;
    }

    FloatNoUnitType realWithError;
    FloatNoUnitType imaginaryWithError;
}
