package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class NodalPlane {

    public NodalPlane(final XMLEventReader reader, String elementName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.strike)) {
                    strike = new RealQuantity(reader, QuakeMLTagNames.strike);
                } else if (elName.equals(QuakeMLTagNames.dip)) {
                    dip = new RealQuantity(reader, QuakeMLTagNames.dip);
                } else if (elName.equals(QuakeMLTagNames.rake)) {
                    rake = new RealQuantity(reader, QuakeMLTagNames.rake);
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

    public RealQuantity getStrike() {
        return strike;
    }

    public RealQuantity getDip() {
        return dip;
    }

    public RealQuantity getRake() {
        return rake;
    }

    RealQuantity strike;

    RealQuantity dip;

    RealQuantity rake;
}
