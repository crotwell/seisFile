package edu.sc.seis.seisFile.stationxml;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.syncFile.SyncFile;
import edu.sc.seis.seisFile.syncFile.SyncFileCompareTest;


public class BasicStationXMLReadTest {

    @Test
    public void testNetworkLevel() throws IOException, SeisFileException, XMLStreamException {
        FDSNStationXML fdsnStationXML = loadStationXML("fdsnstation_network.xml");
        assertTrue("schema version "+fdsnStationXML.getSchemaVersion()+"  "+StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION, fdsnStationXML.checkSchemaVersion());
        NetworkIterator it = fdsnStationXML.getNetworks();
        while (it.hasNext()) {
            Network n = it.next();
            StationIterator sit = n.getStations();
            while (sit.hasNext()) {
                Station s = sit.next();
            }
        }
    }
    @Test
    public void testStationLevel() throws IOException, SeisFileException, XMLStreamException {
        FDSNStationXML fdsnStationXML = loadStationXML("fdsnstation_station.xml");
        assertTrue("schema version "+fdsnStationXML.getSchemaVersion()+"  "+StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION, fdsnStationXML.checkSchemaVersion());
        NetworkIterator it = fdsnStationXML.getNetworks();
        while (it.hasNext()) {
            Network n = it.next();
            StationIterator sit = n.getStations();
            while (sit.hasNext()) {
                Station s = sit.next();
            }
        }
    }
    @Test
    public void testChannelLevel() throws IOException, SeisFileException, XMLStreamException {
        FDSNStationXML fdsnStationXML = loadStationXML("fdsnstation_channel.xml");
        assertTrue("schema version "+fdsnStationXML.getSchemaVersion()+"  "+StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION, fdsnStationXML.checkSchemaVersion());
        NetworkIterator it = fdsnStationXML.getNetworks();
        while (it.hasNext()) {
            Network n = it.next();
            StationIterator sit = n.getStations();
            while (sit.hasNext()) {
                Station s = sit.next();
            }
        }
    }
    @Test
    public void testResponseLevel() throws IOException, SeisFileException, XMLStreamException {
        FDSNStationXML fdsnStationXML = loadStationXML("fdsnstation_response.xml");
        assertTrue("schema version "+fdsnStationXML.getSchemaVersion()+"  "+StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION, fdsnStationXML.checkSchemaVersion());
        NetworkIterator it = fdsnStationXML.getNetworks();
        while (it.hasNext()) {
            Network n = it.next();
            StationIterator sit = n.getStations();
            while (sit.hasNext()) {
                Station s = sit.next();
            }
        }
    }
    
    static FDSNStationXML loadStationXML(String filename) throws XMLStreamException, IOException, SeisFileException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(filename, loadResource(filename));
        XMLEvent e = r.peek();
        while (!e.isStartElement()) {
            e = r.nextEvent(); // eat this one
            e = r.peek(); // peek at the next
        }
        System.out.println("StaMessage");
        FDSNStationXML fdsnStationXML = new FDSNStationXML(r);
        return fdsnStationXML;
    }

    static BufferedInputStream loadResource(String filename) throws IOException, SeisFileException {
        URL url = BasicStationXMLReadTest.class.getClassLoader()
        .getResource("edu/sc/seis/seisFile/stationxml/" + filename);
        System.out.println("resource: "+url);
        return new BufferedInputStream(BasicStationXMLReadTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/stationxml/" + filename));
    }
}
