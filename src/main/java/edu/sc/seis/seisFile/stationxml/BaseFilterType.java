package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public abstract class BaseFilterType {

    void parseAttributes(StartElement startE) throws StationXMLException {
        resourceId = StaxUtil.pullAttribute(startE, StationXMLTagNames.RESOURCEID);
        name = StaxUtil.pullAttribute(startE, StationXMLTagNames.NAME);
    }

    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
            description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
            return true;
        } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
            inputUnits = new Unit(reader);
            return true;
        } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
            outputUnits = new Unit(reader);
            return true;
        } else {
            return false;
        }
    }

    public Unit getInputUnits() {
        return inputUnits;
    }

    public Unit getOutputUnits() {
        return outputUnits;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private String resourceId;

    private String name;

    private String description;

    protected Unit inputUnits;

    protected Unit outputUnits;
}
