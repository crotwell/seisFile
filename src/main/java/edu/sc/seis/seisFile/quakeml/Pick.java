package edu.sc.seis.seisFile.quakeml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.stationxml.StaxUtil;


public class Pick {

    public Pick(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.origin, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.waveformID)) {
                    waveformID = StaxUtil.pullText(reader, QuakeMLTagNames.waveformID);
                } else if (elName.equals(QuakeMLTagNames.time)) {
                    time = new Time(reader);
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
    
    
    public String getPublicId() {
        return publicId;
    }

    
    public Time getTime() {
        return time;
    }

    
    public String getWaveformID() {
        return waveformID;
    }


    private String publicId;
    
    private Time time;
    
    private String waveformID;
}
