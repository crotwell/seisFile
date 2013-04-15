package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;

public class RealQuantity {

    public RealQuantity(float value) {
        this.value = value;
    }
    
    public RealQuantity(final XMLEventReader reader, String tagName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.value)) {
                    value = Float.parseFloat(StaxUtil.pullText(reader, QuakeMLTagNames.value));
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

    public Float getValue() {
        return value;
    }

    Float value;
}
