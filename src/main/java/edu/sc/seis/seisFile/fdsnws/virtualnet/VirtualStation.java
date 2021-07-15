package edu.sc.seis.seisFile.fdsnws.virtualnet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class VirtualStation {

    public VirtualStation(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(VirtualNetTagNames.VIRTUAL_STATION, reader);
        code = StaxUtil.pullAttribute(startE, VirtualNetTagNames.CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(VirtualNetTagNames.vnetStart)) {
                    vnetStart = StaxUtil.pullText(reader, VirtualNetTagNames.vnetStart);
                } else if (elName.equals(VirtualNetTagNames.vnetEnd)) {
                    vnetEnd = StaxUtil.pullText(reader, VirtualNetTagNames.vnetEnd);
                } else if (elName.equals(VirtualNetTagNames.primary_dc)) {
                    primaryDC = StaxUtil.pullText(reader, VirtualNetTagNames.primary_dc);
                } else if (elName.equals(VirtualNetTagNames.certDate)) {
                    certDate = StaxUtil.pullText(reader, VirtualNetTagNames.certDate);
                } else if (elName.equals(VirtualNetTagNames.installDate)) {
                    installDate = StaxUtil.pullText(reader, VirtualNetTagNames.installDate);
                } else {
                    System.err.println("VirtualNetwork skipping " + elName);
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
    
    
    public String getCode() {
        return code;
    }
    
    public String getVnetStart() {
        return vnetStart;
    }
    
    public String getVnetEnd() {
        return vnetEnd;
    }
    
    public String getPrimaryDC() {
        return primaryDC;
    }
    
    public String getCertDate() {
        return certDate;
    }
    
    public String getInstallDate() {
        return installDate;
    }

    String code;
    String vnetStart;
    String vnetEnd;
    String primaryDC;
    String certDate;
    String installDate;
    

                                                          
}
