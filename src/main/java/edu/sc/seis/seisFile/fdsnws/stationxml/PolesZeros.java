package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.Pole;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.Zero;


public class PolesZeros extends BaseFilterType {

    private String pzTransferType;
    private float normalizationFactor;
    private float normalizationFreq;
    private List<Pole> poleList = new ArrayList<Pole>();
    private List<Zero> zeroList = new ArrayList<Zero>();
    public static final String DIGITAL = "DIGITAL (Z-TRANSFORM)";
    public static final String LAPLACE_HERTZ = "LAPLACE (HERTZ)";
    public static final String LAPLACE_RAD_PER_SEC = "LAPLACE (RADIANS/SECOND)";

    
    public PolesZeros(String resourceId,
                      String name,
                      String description,
                      Unit inputUnits,
                      Unit outputUnits,
                      String pzTransferType,
                      float normalizationFactor,
                      float normalizationFreq,
                      List<Zero> zeroList,
                      List<Pole> poleList) {
        super(resourceId, name, description, inputUnits, outputUnits);
        this.pzTransferType = pzTransferType;
        this.normalizationFactor = normalizationFactor;
        this.normalizationFreq = normalizationFreq;
        this.zeroList = zeroList;
        this.poleList = poleList;
    }

    public PolesZeros(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.POLESZEROS, reader);
        super.parseAttributes(startE);
        while(reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // handle buy super
                } else if (elName.equals(StationXMLTagNames.PZTRANSFERTYPE)) {
                    pzTransferType = StaxUtil.pullText(reader, StationXMLTagNames.PZTRANSFERTYPE);
                } else if (elName.equals(StationXMLTagNames.NORMALIZATIONFACTOR)) {
                    normalizationFactor = StaxUtil.pullFloat(reader, StationXMLTagNames.NORMALIZATIONFACTOR);
                } else if (elName.equals(StationXMLTagNames.NORMALIZATIONFREQ)) {
                    normalizationFreq = StaxUtil.pullFloat(reader, StationXMLTagNames.NORMALIZATIONFREQ);
                } else if (elName.equals(StationXMLTagNames.POLE)) {
                    poleList.add( new Pole(reader));
                } else if (elName.equals(StationXMLTagNames.ZERO)) {
                    zeroList.add( new Zero(reader));
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
    
    public String getPzTransferType() {
        return pzTransferType;
    }


    public float getNormalizationFactor() {
        return normalizationFactor;
    }

    
    public float getNormalizationFreq() {
        return normalizationFreq;
    }

    
    public List<Pole> getPoleList() {
        return poleList;
    }

    
    public List<Zero> getZeroList() {
        return zeroList;
    }
    
    
}
