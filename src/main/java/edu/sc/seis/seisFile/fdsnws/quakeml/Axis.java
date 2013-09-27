package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Axis {

    public Axis(final XMLEventReader reader, String elementName) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(elementName, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.azimuth)) {
                    azimuth = new RealQuantity(reader, QuakeMLTagNames.azimuth);
                } else if (elName.equals(QuakeMLTagNames.plunge)) {
                    plunge = new RealQuantity(reader, QuakeMLTagNames.plunge);
                } else if (elName.equals(QuakeMLTagNames.length)) {
                    length = new RealQuantity(reader, QuakeMLTagNames.length);
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

    public RealQuantity getAzimuth() {
        return azimuth;
    }

    public RealQuantity getPlunge() {
        return plunge;
    }

    public RealQuantity getLength() {
        return length;
    }

    RealQuantity azimuth;

    RealQuantity plunge;

    RealQuantity length;
}
