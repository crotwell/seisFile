package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class StaxUtil {

    public static String pullText(XMLEventReader reader, String elementName) throws XMLStreamException,
            StationXMLException {
        String outText = "";
        XMLEvent startElement = reader.nextEvent();
        if (startElement.isStartElement() && startElement.asStartElement().getName().getLocalPart().equals(elementName)) {
            while (reader.hasNext()) {
                XMLEvent e = reader.nextEvent();
                if (e.isCharacters()) {
                    outText += e.asCharacters().getData();
                } else if (e.isEndElement()) {
                    return outText;
                }
            }
            throw new StationXMLException("Ran out of XMLEvents before end of text element");
        } else {
            throw new StationXMLException("Expected START_ELEMENT of type " + elementName);
        }
    }

    public static int pullInt(XMLEventReader reader, String elementName) throws NumberFormatException,
            XMLStreamException, StationXMLException {
        return Integer.parseInt(pullText(reader, elementName));
    }

    public static float pullFloat(XMLEventReader reader, String elementName) throws NumberFormatException,
            XMLStreamException, StationXMLException {
        return Float.parseFloat(pullText(reader, elementName));
    }

    public static void skipToStartElement(XMLEventReader reader) throws XMLStreamException {
        if (! reader.hasNext()) { return; }
        while (reader.hasNext() && ! reader.peek().isStartElement()) {
            reader.nextEvent(); // pop this one
        }
    }

    public static void skipToMatchingEnd(XMLEventReader reader) throws XMLStreamException {
        int count = 0;
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && reader.hasNext()) {
            count++;
            reader.nextEvent(); // pop this one
        }
        while (count > 0 && reader.hasNext()) {
            cur = reader.peek();
            if (cur.isStartElement()) {
                count++;
            } else if (cur.isEndElement()) {
                count--;
            }
            reader.nextEvent(); // pop this one
        }
    }
}
