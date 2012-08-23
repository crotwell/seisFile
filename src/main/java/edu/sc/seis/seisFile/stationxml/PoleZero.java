package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.sac.Complex;


public abstract class PoleZero extends Complex {
    

    public PoleZero(XMLEventReader reader, String elementName) throws XMLStreamException, StationXMLException {
        super(0,0); // dummy values
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.REAL)) {
                    real = StaxUtil.pullFloat(reader, StationXMLTagNames.REAL);
                } else if (elName.equals(StationXMLTagNames.IMAGINARY)) {
                    imaginary = StaxUtil.pullFloat(reader, StationXMLTagNames.IMAGINARY);
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
