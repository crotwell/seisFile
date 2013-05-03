package edu.sc.seis.seisFile.fdsnws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;

public class StaxUtil {

    public static StartElement expectStartElement(String expected, XMLEventReader reader) throws XMLStreamException, StationXMLException {
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals(expected)) {
            parent = expected;
            return reader.nextEvent().asStartElement();
        } else {
            Location loc = cur.getLocation();
            throw new StationXMLException("Expected a start <"+expected+"> element at line "+loc.getLineNumber()+", "+loc.getColumnNumber()+": "+
                                          (cur.isStartElement()?cur.asStartElement().getName().getLocalPart():cur.getEventType()));
        }
    }
    
    public static String pullContiguousText(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        String outText = "";
        while (reader.hasNext()) {
            XMLEvent e = reader.nextEvent();
            if (e.isCharacters()) {
                outText += e.asCharacters().getData();
            } else if (e.isEndElement()) {
                return outText;
            }
        }
        throw new StationXMLException("Ran out of XMLEvents before end of text element");
    }
    
    public static String pullText(XMLEventReader reader, String elementName) throws XMLStreamException,
            StationXMLException {
        String outText = "";
        XMLEvent startElement = StaxUtil.expectStartElement(elementName, reader);
        if (startElement.isStartElement() && startElement.asStartElement().getName().getLocalPart().equals(elementName)) {
            return pullContiguousText(reader);
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
            System.out.println("Warning: Skipping: '"+cur.asStartElement().getName().getLocalPart()
                               +"' at line "+cur.getLocation().getLineNumber()+", "+cur.getLocation().getColumnNumber()
                               +" in '"+parent+"'");
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
    
    public static void skipToStartOrEndElement(XMLEventReader reader) throws XMLStreamException {
        if (! reader.hasNext()) { return; }
        while (reader.hasNext() && ! (reader.peek().isStartElement() || reader.peek().isEndElement()) ) {
            reader.nextEvent(); // pop this one
        }
    }
    
    /** Checks for a next element of name "elementName". Skips over any other elements so
     * long as it doesn't hit an element of name "endElementName. This is so we don't
     *  return stations from the next network when passing a ending network tag.
     * @param reader 
     * @param elementName element name we are looking for
     * @param endElementName end element name to not go past, ie the parent element
     * @return true if there is another element, false otherwise
     * @throws XMLStreamException
     */
    public static boolean hasNext(XMLEventReader reader, String elementName, String endElementName) throws XMLStreamException {
        while (reader.hasNext()) {
            if (reader.peek().isStartElement()) {
                if (reader.peek().asStartElement().getName().getLocalPart().equals(elementName)) {
                    return true;
                }
                // humm, unexpected start element so skip this element and go to the next one and try again
                reader.next();
                StaxUtil.skipToMatchingEnd(reader);
            } else if (reader.peek().isEndElement() && reader.peek().asEndElement().getName().getLocalPart().equals(endElementName)) {
                return false;
            } else {
                // some other type of stax event, skip it
                reader.next();
            }
        }
        return false;
    }

    public static boolean hasAttribute(StartElement start, String name) {
        Iterator<Attribute> it = start.getAttributes();
        while(it.hasNext()) {
            Attribute a = it.next();
            if (a.getName().getLocalPart().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String pullAttributeIfExists(StartElement start, String name) throws StationXMLException {
        Iterator<Attribute> it = start.getAttributes();
        while(it.hasNext()) {
            Attribute a = it.next();
            if (a.getName().getLocalPart().equals(name)) {
                return a.getValue();
            }
        }
        return null;
    }
    
    public static String pullAttribute(StartElement start, String name) throws StationXMLException {
        String val = pullAttributeIfExists(start, name);
        if (val != null) {
            return val;
        }
        throw new StationXMLException(name+" not found as an attribute in "+start.getName().getLocalPart()
                                      +" at "+start.getLocation().getLineNumber()+", "+start.getLocation().getColumnNumber());
    }


    public static Integer pullIntAttribute(StartElement start, String name) throws StationXMLException {
        return Integer.parseInt(pullAttribute(start, name));
    }

    public static Float pullFloatAttribute(StartElement start, String name) throws StationXMLException {
        return Float.parseFloat(pullAttribute(start, name));
    }

    public static Date pullDate(XMLEventReader reader, String name) throws StationXMLException, XMLStreamException {
        return parseDate(pullText(reader, name));
    }
    
    /** extracts a Date from the named attribute. Null if the attribute is not found. */
    public static Date pullDateAttributeIfExists(StartElement start, String name) throws StationXMLException {
        return parseDate(pullAttributeIfExists(start, name));
    }
    
    public static Date parseDate(String text) throws StationXMLException {
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_DATE_FORMAT);
        try {
            return sdf.parse(text);
        } catch(ParseException e) {
            sdf = new SimpleDateFormat(DATE_FORMAT);
            try {
                return sdf.parse(text);
            } catch(ParseException e1) {
                throw new StationXMLException(e);
            }
        }
    }
    
    static String parent = "";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
}
