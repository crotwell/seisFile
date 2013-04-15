package edu.sc.seis.seisFile.fdsnws.stationxml;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;


public class TestNetworkLevel {

    @Test
    public void test() throws XMLStreamException, IOException, SeisFileException {
        FDSNStationXML fdsnStationXML = BasicStationXMLReadTest.loadStationXML("fdsnstation_network.xml");
        assertEquals("source", "IRIS-DMC", fdsnStationXML.getSource());
        assertEquals("sender", "IRIS-DMC", fdsnStationXML.getSender());
        assertEquals("module", "IRIS WEB SERVICE: fdsnws-station | version: 1.0.2", fdsnStationXML.getModule());
        assertEquals("moduleUri", "http://service.iris.edu/fdsnws/station/1/query?level=network", fdsnStationXML.getModuleUri());
        assertEquals("created", "2013-04-09T14:29:03", fdsnStationXML.getCreated());
        NetworkIterator netIt = fdsnStationXML.getNetworks();
        while(netIt.hasNext()) {
            Network net = netIt.next();
            assertNotNull(net.getDescription());
            assertNotNull(net.getCode());
            assertEquals("selected stations", 0, net.getSelectedNumStations());
            if (net.getCode().equals("CO")) {
                assertEquals("Description", "South Carolina Seismic Network", net.getDescription());
                assertEquals("totStations", 8, net.getTotalNumStations());
            }
            assertFalse("stations", net.getStations().hasNext());
        }
    }
}
