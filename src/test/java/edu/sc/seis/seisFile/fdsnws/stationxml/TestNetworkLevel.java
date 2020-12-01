package edu.sc.seis.seisFile.fdsnws.stationxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import javax.xml.stream.XMLStreamException;


import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;


public class TestNetworkLevel {

    @Test
    public void test() throws XMLStreamException, IOException, SeisFileException {
        FDSNStationXML fdsnStationXML = BasicStationXMLReadTest.loadStationXML("fdsnstation_network.xml");
        assertEquals("IRIS-DMC", fdsnStationXML.getSource());
        assertEquals("IRIS-DMC", fdsnStationXML.getSender());
        assertEquals( "IRIS WEB SERVICE: fdsnws-station | version: 1.0.2", fdsnStationXML.getModule());
        assertEquals("http://service.iris.edu/fdsnws/station/1/query?level=network", fdsnStationXML.getModuleUri());
        assertEquals( "2013-04-09T14:29:03", fdsnStationXML.getCreated());
        NetworkIterator netIt = fdsnStationXML.getNetworks();
        while(netIt.hasNext()) {
            Network net = netIt.next();
            assertNotNull(net.getDescription());
            assertNotNull(net.getCode());
            assertEquals(0, net.getSelectedNumStations());
            if (net.getCode().equals("CO")) {
                assertEquals( "South Carolina Seismic Network", net.getDescription());
                assertEquals( 8, net.getTotalNumStations());
            }
            assertFalse( net.getStations().hasNext());
        }
    }
}
