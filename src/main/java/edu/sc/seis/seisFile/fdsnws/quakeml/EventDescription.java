package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


public class EventDescription {
    
    public EventDescription(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.description, reader);
        Attribute feCodeAttr = startE.getAttributeByName(new QName(QuakeMLTagNames.irisNameSpace, QuakeMLTagNames.fecode));
        if (feCodeAttr != null) {
            try {
                irisFECode = Integer.parseInt(feCodeAttr.getValue());
            } catch(NumberFormatException e) {
                throw new SeisFileException("Unable to parse FECode, expected integer but was: "+feCodeAttr.getValue(), e);
            }
        }
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.text)) {
                    text = StaxUtil.pullText(reader, QuakeMLTagNames.text);
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
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
    
    public String toString() {
        return getType()+": "+(getText()==null?"":getText());
    }
    
    public String getText() {
        return text;
    }

    
    public String getType() {
        return type;
    }
    
    public int getIrisFECode() {
        return irisFECode;
    }

    int irisFECode = -1;

    String text, type;
}
