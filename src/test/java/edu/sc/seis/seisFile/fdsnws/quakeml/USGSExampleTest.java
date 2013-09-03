package edu.sc.seis.seisFile.fdsnws.quakeml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;


public class USGSExampleTest {

    @Test
    public void test() throws IOException, SeisFileException, XMLStreamException, StationXMLException {
        String[] filenames = new String[] {"usgs/B000I89Z20Long.quakeml", 
                                           "usgs/C000HX2T18Long.quakeml", 
                                           "usgs/C000HVSF38Long.quakeml", 
                                           "usgs/C000HZFK62Long.quakeml"};
        for (String filename : filenames) {
            
        URL url = QuakeMLTest.loadResourceURL(filename);
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(url.toString(), QuakeMLTest.loadResource(filename));
        
        Quakeml qml = new Quakeml(r);
        EventParameters ep = qml.getEventParameters();
        EventIterator it = ep.getEvents();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        }
    }
}
