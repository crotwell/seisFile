package edu.sc.seis.seisFile.fdsnws;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.sc.seis.seisFile.client.StationClient;
import picocli.CommandLine;

public class StationClientTest {

    @Test
    public void testHostArg() throws Exception {
        String hostname = "service.seis.sc.edu";
        StationClient sc = new StationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"--host", hostname, "-n", "CO"};
        cmd.parseArgs(args);
        assertEquals("http://"+hostname + ":80/fdsnws/station/1/query?network=CO", sc.queryParams.formURI().toString());
    }

    @Test
    public void testNetworkArg() throws Exception {
        StationClient sc = new StationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO", sc.queryParams.formURI().toString());
    }

    @Test
    public void testStationArg() throws Exception {
        StationClient sc = new StationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC", sc.queryParams.formURI().toString());
    }

    @Test
    public void testTwoStationArg() throws Exception {
        StationClient sc = new StationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC,CASEE"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC,CASEE", sc.queryParams.formURI().toString());
    }
    


    @Test
    public void testLotsOfArg() throws Exception {
        StationClient sc = new StationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String argString = "-n CO -s JSC,CASEE -l 00 --network II --endafter 2010-01-02 --box 30/35/-100/-75 --donut 32/-85/0/5 --level response --start 2009";
        String[] args = argString.split(" ");
        cmd.parseArgs(args);
        assertEquals("CO,II", sc.queryParams.getParam(FDSNStationQueryParams.NETWORK));
        assertEquals("JSC,CASEE", sc.queryParams.getParam(FDSNStationQueryParams.STATION));
        assertEquals("2010-01-02T00:00:00Z", sc.queryParams.getParam(FDSNStationQueryParams.ENDAFTER));
       // assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC,CASEE", queryParams.formURI().toString());
    }
}
