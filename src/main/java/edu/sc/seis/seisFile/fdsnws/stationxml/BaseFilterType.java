package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;

public abstract class BaseFilterType {

    void parseAttributes(StartElement startE) throws StationXMLException {
        resourceId = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.RESOURCEID);
        name = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.NAME);
    }

    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
            description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
            return true;
        } else if (elName.equals(StationXMLTagNames.INPUTUNITS)) {
            inputUnits = new Unit(reader, StationXMLTagNames.INPUTUNITS);
            return true;
        } else if (elName.equals(StationXMLTagNames.OUTPUTUNITS)) {
            outputUnits = new Unit(reader, StationXMLTagNames.OUTPUTUNITS);
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
