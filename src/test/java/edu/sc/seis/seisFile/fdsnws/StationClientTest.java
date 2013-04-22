package edu.sc.seis.seisFile.fdsnws;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;

import edu.sc.seis.seisFile.SeisFileException;


public class StationClientTest {

    @Test
    public void testNetworkArg() throws JSAPException {
        StationClient sc = new StationClient(new String[] {"-n", "CO"}) {

            @Override
            public void process(URI uri) throws IOException, XMLStreamException, SeisFileException {
                assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI+"network=CO", uri.toString());
            }
            
        };
        sc.run();
    }

    @Test
    public void testStationArg() throws JSAPException {
        StationClient sc = new StationClient(new String[] {"-n", "CO", "-s", "JSC"}) {

            @Override
            public void process(URI uri) throws IOException, XMLStreamException, SeisFileException {
                assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI+"network=CO&station=JSC", uri.toString());
            }
            
        };
        sc.run();
    }
    @Test
    public void testTwoStationArg() throws JSAPException {
        StationClient sc = new StationClient(new String[] {"-n", "CO", "-s", "JSC,CASEE"}) {

            @Override
            public void process(URI uri) throws IOException, XMLStreamException, SeisFileException {
                assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI+"network=CO&station=JSC,CASEE", uri.toString());
            }
            
        };
        sc.run();
    }
}
