package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class PrincipalAxes {

    public static final String ELEMENT_NAME = QuakeMLTagNames.principalAxes;

    public PrincipalAxes(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.tAxis)) {
                    tAxis = new Axis(reader, QuakeMLTagNames.tAxis);
                } else if (elName.equals(QuakeMLTagNames.pAxis)) {
                    pAxis = new Axis(reader, QuakeMLTagNames.pAxis);
                } else if (elName.equals(QuakeMLTagNames.nAxis)) {
                    nAxis = new Axis(reader, QuakeMLTagNames.nAxis);
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

    public Axis getTAxis() {
        return tAxis;
    }

    public Axis getPAxis() {
        return pAxis;
    }

    public Axis getNAxis() {
        return nAxis;
    }

    Axis tAxis;

    Axis pAxis;

    Axis nAxis;
}
