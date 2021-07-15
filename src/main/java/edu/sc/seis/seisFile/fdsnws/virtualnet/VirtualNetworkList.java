package edu.sc.seis.seisFile.fdsnws.virtualnet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.TimeQueryLog;

public class VirtualNetworkList {

    private VirtualNetworkList() {}
    
    public VirtualNetworkList(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(VirtualNetTagNames.VIRTUAL_NETWORKS, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(VirtualNetTagNames.SOURCE)) {
                    source = StaxUtil.pullText(reader, VirtualNetTagNames.SOURCE);
                } else if (elName.equals(VirtualNetTagNames.SENDER)) {
                    sender = StaxUtil.pullText(reader, VirtualNetTagNames.SENDER);
                } else if (elName.equals(VirtualNetTagNames.MODULE)) {
                    module = StaxUtil.pullText(reader, VirtualNetTagNames.MODULE);
                } else if (elName.equals(VirtualNetTagNames.SENT_DATE)) {
                    sentDate = StaxUtil.pullText(reader, VirtualNetTagNames.SENT_DATE);
                } else if (elName.equals(VirtualNetTagNames.DATA_CENTER)) {
                    dataCenters.add(new ContributorDataCenter(reader));
                } else if (elName.equals(VirtualNetTagNames.VIRTUAL_NETWORK)) {
                    virtualNetworks.add(new VirtualNetwork(reader));
                    break;
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
    
    
    public String getSource() {
        return source;
    }

    
    public String getSender() {
        return sender;
    }

    
    public String getModule() {
        return module;
    }

    
    public String getSentDate() {
        return sentDate;
    }

    public List<ContributorDataCenter> getDataCenters() { return dataCenters;}
    
    public List<VirtualNetwork> getVirtualNetworks() {
        return virtualNetworks;
    }

    String source;
    String sender;
    String module;
    String sentDate;

    List<VirtualNetwork> virtualNetworks = new ArrayList<VirtualNetwork>();
    
    List<ContributorDataCenter> dataCenters = new ArrayList<ContributorDataCenter>();

    public static VirtualNetworkList createEmptyVirtualNets() {
        return new VirtualNetworkList();
    }
    
}
