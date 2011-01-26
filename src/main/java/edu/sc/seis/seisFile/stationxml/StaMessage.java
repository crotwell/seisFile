package edu.sc.seis.seisFile.stationxml;


import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class StaMessage {
    
    public StaMessage(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StaxUtil.skipToStartElement(reader);
        XMLEvent cur = reader.peek();
        if (cur.isStartElement() && cur.asStartElement().getName().getLocalPart().equals("StaMessage")) {
            XMLEvent e = reader.nextEvent(); // pop StaMessage
            while(reader.hasNext()) {
                e = reader.peek();
                if (e.isStartElement()) {
                    String elName = e.asStartElement().getName().getLocalPart();
                    System.out.println("StaMessage <"+elName+">");
                    if (elName.equals(SOURCE)) {
                        source = StaxUtil.pullText(reader, SOURCE);
                    } else if (elName.equals(SENDER)) {
                        sender = StaxUtil.pullText(reader, SENDER);
                    } else if (elName.equals(MODULE)) {
                        module = StaxUtil.pullText(reader, MODULE);
                    } else if (elName.equals(SENTDATE)) {
                        sentDate = StaxUtil.pullText(reader, SENTDATE);
                    } else if (elName.equals("Station")) {
                        stations = new StationIterator(reader);
                        break;
                    } else {
                        StaxUtil.skipToMatchingEnd(reader);
                    }
                } else if (e.isEndElement()) {
                    return;
                } else {
                    e = reader.nextEvent();
                }
            }
        } else {
            if (cur.isStartElement()) {
                throw new StationXMLException("Not a StartElement: "+cur.isStartElement());
            } else {
                throw new StationXMLException("Not a Station element: "+cur.asStartElement().getName().getLocalPart());
            }
        }
    }

    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public String getSentDate() {
        return sentDate;
    }
    
    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }
    
    public StationIterator getStations() {
        return stations;
    }

    public static final String NAMESPACE = "http://www.data.scec.org/xml/station/";
    public static final String SOURCE = "Source";
    public static final String SENDER = "Sender";
    public static final String MODULE = "Module";
    public static final String SENTDATE = "SentDate";
    public static final String STATION = "Station";
    public static final String NET_CODE = "net_code";
    public static final String STA_CODE = "sta_code";
    public static final String SITE = "Site";

    String source;
    String sender;
    String module;
    String sentDate;
    StationIterator stations;
}
