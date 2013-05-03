package edu.sc.seis.seisFile.fdsnws;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.martiansoftware.jsap.JSAPResult;

public class StationClientTest {

    @Test
    public void testNetworkArg() throws Exception {
        StationClient sc = new StationClient(new String[] {"-n", "CO"});
        JSAPResult result = sc.getResult();
        FDSNStationQueryParams queryParams = sc.configureQuery(result);
        assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI + "network=CO", queryParams.formURI().toString());
    }

    @Test
    public void testStationArg() throws Exception {
        StationClient sc = new StationClient(new String[] {"-n", "CO", "-s", "JSC"});
        JSAPResult result = sc.getResult();
        FDSNStationQueryParams queryParams = sc.configureQuery(result);
        assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI + "network=CO&station=JSC", queryParams.formURI().toString());
    }

    @Test
    public void testTwoStationArg() throws Exception {
        StationClient sc = new StationClient(new String[] {"-n", "CO", "-s", "JSC,CASEE"});
        JSAPResult result = sc.getResult();
        FDSNStationQueryParams queryParams = sc.configureQuery(result);
        assertEquals("uri", FDSNStationQueryParams.IRIS_BASE_URI + "network=CO&station=JSC,CASEE", queryParams.formURI().toString());
    }
}
