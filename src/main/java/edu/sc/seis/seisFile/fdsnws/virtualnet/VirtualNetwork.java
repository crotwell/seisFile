package edu.sc.seis.seisFile.fdsnws.virtualnet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class VirtualNetwork {

    public VirtualNetwork(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(VirtualNetTagNames.VIRTUAL_NETWORK, reader);
        code = StaxUtil.pullAttribute(startE, VirtualNetTagNames.CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(VirtualNetTagNames.START)) {
                    start = StaxUtil.pullText(reader, VirtualNetTagNames.START);
                } else if (elName.equals(VirtualNetTagNames.END)) {
                    end = StaxUtil.pullText(reader, VirtualNetTagNames.END);
                } else if (elName.equals(VirtualNetTagNames.DESCRIPTION)) {
                    description = StaxUtil.pullText(reader, VirtualNetTagNames.DESCRIPTION);
                } else if (elName.equals(VirtualNetTagNames.DEFINITION)) {
                    definition = StaxUtil.pullText(reader, VirtualNetTagNames.DEFINITION);
                } else if (elName.equals(VirtualNetTagNames.LAST_UPDATED)) {
                    lastUpdated = StaxUtil.pullText(reader, VirtualNetTagNames.LAST_UPDATED);
                } else if (elName.equals(VirtualNetTagNames.STEWARD)) {
                    steward = StaxUtil.pullText(reader, VirtualNetTagNames.STEWARD);
                } else if (elName.equals(VirtualNetTagNames.CERT_DATE)) {
                    certDate = StaxUtil.pullText(reader, VirtualNetTagNames.CERT_DATE);
                } else if (elName.equals(VirtualNetTagNames.INSTALL_DATE)) {
                    installDate = StaxUtil.pullText(reader, VirtualNetTagNames.INSTALL_DATE);
                } else if (elName.equals(VirtualNetTagNames.NETWORK)) {
                    contribNetList.add( new ContributorNetwork(reader));
                    break;
                } else {
                    System.err.println("VirtualNetwork skipping "+elName);
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

    
    public String getStart() {
        return start;
    }

    
    public String getEnd() {
        return end;
    }

    
    public String getDescription() {
        return description;
    }

    
    public String getDefinition() {
        return definition;
    }

    
    public String getLastUpdated() {
        return lastUpdated;
    }

    
    public String getSteward() {
        return steward;
    }

    
    public String getCertDate() {
        return certDate;
    }

    
    public String getInstallDate() {
        return installDate;
    }

    
    public List<ContributorNetwork> getContribNetList() {
        return contribNetList;
    }


    String code;
    String start;
    String end;
    String description;
    String definition;
    String lastUpdated;
    String steward;
    String certDate;
    String installDate;
    
    List<ContributorNetwork> contribNetList = new ArrayList<ContributorNetwork>();
    
                                                          
}
