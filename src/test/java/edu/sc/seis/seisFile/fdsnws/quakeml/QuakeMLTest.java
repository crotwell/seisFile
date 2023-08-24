package edu.sc.seis.seisFile.fdsnws.quakeml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventParameters;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;


public class QuakeMLTest {

    @Test
    public void test() throws IOException, SeisFileException, XMLStreamException, StationXMLException {
        String filename = "minmag8_5.quakeml";
        URL url = loadResourceURL(filename);
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(url.toString(), loadResource(filename));
        
        Quakeml qml = new Quakeml(r);
        EventParameters ep = qml.getEventParameters();
        EventIterator it = ep.getEvents();
        
        checkEvent(0, it, "2012-04-11T08:38:36.7200", 2.327f, 93.063f, 20f, 8.6f);
        checkEvent(1, it, "2011-03-11T05:46:24.1200", 38.297f, 142.373f, 29.0f, 9.1f);
        checkEvent(2, it, "2010-02-27T06:34:13.3300", -36.1485f, -72.9327f, 28.1f, 8.8f);
        checkEvent(3, it, "2007-09-12T11:10:26.8700", -4.4637f, 101.3959f, 35.5f, 8.5f);
        checkEvent(4, it, "2005-03-28T16:09:35.2900", 2.0964f, 97.1131f, 30.0f, 8.6f);
        checkEvent(5, it, "2004-12-26T00:58:52.0500", 3.4125f, 95.9012f, 26.1f, 9.0f);
        
        
    }


    @Test
    public void test_noloc() throws IOException, SeisFileException, XMLStreamException, StationXMLException {
        String filename = "noloc_pick.qml";
        URL url = loadResourceURL(filename);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(url.toString(), loadResource(filename));

        Quakeml qml = new Quakeml(r);
        EventParameters ep = qml.getEventParameters();
        EventIterator it = ep.getEvents();
        Event e = it.next();
        for (Pick p : e.getPickList()) {
            WaveformStreamID wid = p.getWaveformID();
            assertEquals(wid.locationCode == null, wid.channelCode == null, "loc null only if chan is too");
        }
    }
    
    private void checkEvent(int num, EventIterator it, String time, float lat, float lon, float depth, float mag) throws XMLStreamException, SeisFileException {
        assertTrue(it.hasNext(), "has event");
        Event e = it.next();
        assertEquals( 1, e.getOriginList().size(), num+" num origins");
        Origin o = e.getOriginList().get(0);
        System.out.println(e.getDescriptionList().get(0)+" "+o.toString());
        assertEquals( time, o.getTime().getValue(), num+" time");
        assertEquals( lat, o.getLatitude().getValue(), 0.000001f, num+" lat");
        assertEquals( lon, o.getLongitude().getValue(), 0.000001f, num+" lon");
        assertEquals(depth, o.getDepth().getValue(), 0.000001f, num+" depth");
        assertEquals( 1, e.getMagnitudeList().size(), num+" num magnitudes");
        Magnitude m = e.getMagnitudeList().get(0);
        assertEquals( mag, m.getMag().getValue(), 0.000001f, num+" mag");
    }


    static BufferedReader loadResource(String filename) throws IOException, SeisFileException {
        return new BufferedReader(new InputStreamReader(QuakeMLTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/quakeml/" + filename)));
    }
    static URL loadResourceURL(String filename) throws IOException, SeisFileException {
        return QuakeMLTest.class.getClassLoader().getResource("edu/sc/seis/seisFile/quakeml/" + filename);
    }
}
