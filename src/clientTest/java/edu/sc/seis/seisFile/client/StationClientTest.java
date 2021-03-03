package edu.sc.seis.seisFile.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;
import picocli.CommandLine;

public class StationClientTest {

    @Test
    public void testHostArg() throws Exception {
        String hostname = "service.seis.sc.edu";
        FDSNStationClient sc = new FDSNStationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"--host", hostname, "-n", "CO"};
        cmd.parseArgs(args);
        assertEquals("http://"+hostname + ":80/fdsnws/station/1/query?network=CO", sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testNetworkArg() throws Exception {
        FDSNStationClient sc = new FDSNStationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO", sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testStationArg() throws Exception {
        FDSNStationClient sc = new FDSNStationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC", sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testTwoStationArg() throws Exception {
        FDSNStationClient sc = new FDSNStationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC,CASEE"};
        cmd.parseArgs(args);
        assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC,CASEE", sc.getQueryParams().formURI().toString());
    }



    @Test
    public void testLotsOfArg() throws Exception {
        FDSNStationClient sc = new FDSNStationClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String argString = "-n CO -s JSC,CASEE -l 00 --network II --endafter 2010-01-02 --box 30/35/-100/-75 --donut 32/-85/0/5 --level response --start 2009";
        String[] args = argString.split(" ");
        cmd.parseArgs(args);
        assertEquals("CO,II", sc.getQueryParams().getParam(FDSNStationQueryParams.NETWORK));
        assertEquals("JSC,CASEE", sc.getQueryParams().getParam(FDSNStationQueryParams.STATION));
        assertEquals("2010-01-02T00:00:00.000Z", sc.getQueryParams().getParam(FDSNStationQueryParams.ENDAFTER));
       // assertEquals("http://"+FDSNStationQueryParams.IRIS_HOST + ":80/fdsnws/station/1/query?network=CO&station=JSC,CASEE", queryParams.formURI().toString());
    }
}
