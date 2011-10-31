package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class DataLogger extends Equipment {

    public DataLogger(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.DATALOGGER, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.EQUIP_TYPE)) {
                    equipType = StaxUtil.pullText(reader, StationXMLTagNames.EQUIP_TYPE);
                } else if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
                    description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
                } else if (elName.equals(StationXMLTagNames.MANUFACTURER)) {
                    manufacturer = StaxUtil.pullText(reader, StationXMLTagNames.MANUFACTURER);
                } else if (elName.equals(StationXMLTagNames.VENDOR)) {
                    vendor = StaxUtil.pullText(reader, StationXMLTagNames.VENDOR);
                } else if (elName.equals(StationXMLTagNames.MODEL)) {
                    model = StaxUtil.pullText(reader, StationXMLTagNames.MODEL);
                } else if (elName.equals(StationXMLTagNames.SERIALNUMBER)) {
                    serialNumber = StaxUtil.pullText(reader, StationXMLTagNames.SERIALNUMBER);
                } else if (elName.equals(StationXMLTagNames.INSTALLATIONDATE)) {
                    installationDate = StaxUtil.pullText(reader, StationXMLTagNames.INSTALLATIONDATE);
                } else if (elName.equals(StationXMLTagNames.REMOVALDATE)) {
                    removalDate = StaxUtil.pullText(reader, StationXMLTagNames.REMOVALDATE);
                } else if (elName.equals(StationXMLTagNames.CALIBRATIONDATE)) {
                    calibrationDate.add( StaxUtil.pullText(reader, StationXMLTagNames.CALIBRATIONDATE));
                } else if (elName.equals(StationXMLTagNames.TOTALCHANNELS)) {
                    totalChannels = StaxUtil.pullInt(reader, StationXMLTagNames.TOTALCHANNELS);
                } else if (elName.equals(StationXMLTagNames.RECORDEDCHANNELS)) {
                    recordedChannels = StaxUtil.pullInt(reader, StationXMLTagNames.RECORDEDCHANNELS);
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

    
    public int getTotalChannels() {
        return totalChannels;
    }

    
    public int getRecordedChannels() {
        return recordedChannels;
    }

    protected int totalChannels, recordedChannels;
}
