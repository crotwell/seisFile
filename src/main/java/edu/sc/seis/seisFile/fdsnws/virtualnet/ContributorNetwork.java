package edu.sc.seis.seisFile.fdsnws.virtualnet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class ContributorNetwork {

    public ContributorNetwork(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(VirtualNetTagNames.VIRTUAL_NETWORK, reader);
        code = StaxUtil.pullAttribute(startE, VirtualNetTagNames.CODE);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(VirtualNetTagNames.START_YEAR)) {
                    startYear = StaxUtil.pullText(reader, VirtualNetTagNames.START_YEAR);
                } else if (elName.equals(VirtualNetTagNames.END_YEAR)) {
                    endYear = StaxUtil.pullText(reader, VirtualNetTagNames.END_YEAR);
                } else if (elName.equals(VirtualNetTagNames.DESCRIPTION)) {
                    description = StaxUtil.pullText(reader, VirtualNetTagNames.DESCRIPTION);
                } else if (elName.equals(VirtualNetTagNames.NICKNAME)) {
                    nickname = StaxUtil.pullText(reader, VirtualNetTagNames.NICKNAME);
                } else if (elName.equals(VirtualNetTagNames.VIRTUAL_STATION)) {
                    stationList.add(new VirtualStation(reader));
                    break;
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
    
    
    public List<VirtualStation> getStationList() {
        return stationList;
    }
    
    public String getStartYear() {
        return startYear;
    }
    
    public String getEndYear() {
        return endYear;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCode() {
        return code;
    }

    List<VirtualStation> stationList = new ArrayList<VirtualStation>();
    
    String startYear;
    String endYear;
    String nickname;
    String description;
    String code;
}
