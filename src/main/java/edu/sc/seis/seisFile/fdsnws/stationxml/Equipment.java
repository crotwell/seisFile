package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class Equipment {

    public Equipment() {
        
    }
    
    public Equipment(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        this(reader, StationXMLTagNames.EQUIPMENT);
    }
    
    public Equipment(XMLEventReader reader, String tagName) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        parseAttributes(startE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (parseSubElement(elName, reader)) {
                    // super handled it
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

    void parseAttributes(StartElement startE) throws StationXMLException{
        resourceId = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.RESOURCEID);
    }
    
    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.TYPE)) {
            type = StaxUtil.pullText(reader, StationXMLTagNames.TYPE);
            return true;
        } else if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
            description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
            return true;
        } else if (elName.equals(StationXMLTagNames.MANUFACTURER)) {
            manufacturer = StaxUtil.pullText(reader, StationXMLTagNames.MANUFACTURER);
            return true;
        } else if (elName.equals(StationXMLTagNames.VENDOR)) {
            vendor = StaxUtil.pullText(reader, StationXMLTagNames.VENDOR);
            return true;
        } else if (elName.equals(StationXMLTagNames.MODEL)) {
            model = StaxUtil.pullText(reader, StationXMLTagNames.MODEL);
            return true;
        } else if (elName.equals(StationXMLTagNames.SERIALNUMBER)) {
            serialNumber = StaxUtil.pullText(reader, StationXMLTagNames.SERIALNUMBER);
            return true;
        } else if (elName.equals(StationXMLTagNames.INSTALLATIONDATE)) {
            installationDate = StaxUtil.pullText(reader, StationXMLTagNames.INSTALLATIONDATE);
            return true;
        } else if (elName.equals(StationXMLTagNames.REMOVALDATE)) {
            removalDate = StaxUtil.pullText(reader, StationXMLTagNames.REMOVALDATE);
            return true;
        } else if (elName.equals(StationXMLTagNames.CALIBRATIONDATE)) {
            calibrationDate.add(StaxUtil.pullText(reader, StationXMLTagNames.CALIBRATIONDATE));
            return true;
        } else {
            return false;
        }
    }
    
    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public String getModel() {
        return model;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public String getInstallationDate() {
        return installationDate;
    }
    
    public String getRemovalDate() {
        return removalDate;
    }
    
    public List<String> getCalibrationDate() {
        return calibrationDate;
    }
    
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public void setRemovalDate(String removalDate) {
        this.removalDate = removalDate;
    }

    public void setCalibrationDate(List<String> calibrationDate) {
        this.calibrationDate = calibrationDate;
    }

    public void appendCalibrationDate(String o) {
        calibrationDate.add(o);        
    }

    String resourceId;
    
    protected String type, description, manufacturer, vendor, model, serialNumber, installationDate, removalDate;
    protected List<String> calibrationDate = new ArrayList<String>();
}
