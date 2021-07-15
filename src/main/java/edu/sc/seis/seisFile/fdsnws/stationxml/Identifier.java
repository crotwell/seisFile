package edu.sc.seis.seisFile.fdsnws.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Identifier {

    
	public Identifier(String value, String type) {
		this.value = value;
		this.type = type;
	}
	
	public Identifier(XMLEventReader reader, String tagName) throws StationXMLException, XMLStreamException {
        StartElement startE = StaxUtil.expectStartElement(tagName, reader);
        parseAttributes(startE);
        parseValue(reader);
    }

    void parseValue(final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        value = StaxUtil.pullContiguousText(reader);
    }

    void parseAttributes(StartElement startE) throws StationXMLException {
        String typeStr = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.ATTR_TYPE);
        if (typeStr != null) {
            type = typeStr;
        }
    }
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	String value;
	
	String type = null;
}
