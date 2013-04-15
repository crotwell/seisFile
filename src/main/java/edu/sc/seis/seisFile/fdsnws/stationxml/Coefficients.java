package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class Coefficients extends BaseFilterType {

    public Coefficients(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.COEFFICIENTS, reader);
        super.parseAttributes(startE);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // handle buy super
                } else if (elName.equals(StationXMLTagNames.CFTRANSFERTYPE)) {
                    cfTransferType = StaxUtil.pullText(reader, StationXMLTagNames.CFTRANSFERTYPE);
                } else if (elName.equals(StationXMLTagNames.NUMERATOR)) {
                    numeratorList.add( new FloatType(reader, StationXMLTagNames.NUMERATOR));
                } else if (elName.equals(StationXMLTagNames.DENOMINATOR)) {
                    denominatorList.add( new FloatType(reader, StationXMLTagNames.DENOMINATOR));
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else  {
                e = reader.nextEvent();
            }
        }
    }

    public static String getCfTransferType() {
        return cfTransferType;
    }


    public List<FloatType> getNumeratorList() {
        return numeratorList;
    }
    
    public List<FloatType> getDenominatorList() {
        return denominatorList;
    }

    private static String cfTransferType;
    List<FloatType> numeratorList = new ArrayList<FloatType>();
    List<FloatType> denominatorList = new ArrayList<FloatType>();
}
