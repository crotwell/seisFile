package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


/** a float value with optional unit and errors. */
public class FloatType extends FloatNoUnitType {

    public FloatType(XMLEventReader reader, String tagName) throws StationXMLException, XMLStreamException {
        this(reader, tagName, null);
    }

    public FloatType(XMLEventReader reader, String tagName, String fixedUnit) throws StationXMLException, XMLStreamException {
        super(tagName);
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        super.parseAttributes(startE);
        super.parseValue(reader);
        parseUnitAttr(startE, fixedUnit);
    }
    
    void parseUnitAttr(StartElement startE, String fixedUnit) throws StationXMLException {
        String unitStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.UNIT);
        if (unitStr != null) {
            unit = unitStr;
        } else {
            unit = fixedUnit;
        }
    }
    
    public String toString() {
        String out = ""+getValue();
        if (hasPlusError() || hasMinusError()) {
            out += "(";
            if (hasPlusError()) {
                out += "+"+getPlusError();
            }
            if (hasMinusError()) {
                if (hasPlusError()) {
                    out += " ";
                }
                out += getMinusError();
            }
            out += ")";
        }
        return out;
    }
    
    
    public String getUnit() {
        return unit;
    }

    String unit;
}
