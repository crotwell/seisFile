package edu.sc.seis.seisFile.client;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams.LOCATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataSelectClientTest {

    @Test
    public void testHostArg() throws Exception {
        String hostname = "service.seis.sc.edu";
        FDSNDataSelectClient sc = new FDSNDataSelectClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"--host", hostname, "-n", "CO", "-b", "2021-02-28T12:45:00", "-e", "2021-02-28T12:47:00"};
        cmd.parseArgs(args);
        assertEquals("http://"+hostname + ":80/fdsnws/dataselect/1/query?endtime=2021-02-28T12:47:00.999&network=CO&starttime=2021-02-28T12:45:00.000",
                sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testEarthscopeHost() throws Exception {
        String hostname = "service.earthscope.org";
        FDSNDataSelectClient sc = new FDSNDataSelectClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC", "-c", "HHZ", "-b", "2021-02-28T12:45:00", "-e", "2021-02-28T12:47:00"};
        cmd.parseArgs(args);
        assertEquals("https://"+hostname + ":443/fdsnws/dataselect/1/query?channel=HHZ&endtime=2021-02-28T12:47:00.999&network=CO&starttime=2021-02-28T12:45:00.000&station=JSC",
                sc.getQueryParams().formURI().toString());
    }


    @Test
    public void testDashDashLocCode() throws Exception {
        String hostname = "service.earthscope.org";
        FDSNDataSelectClient sc = new FDSNDataSelectClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC", "-c", "HHZ", "-l", "--", "-b", "2021-02-28T12:45:00", "-e", "2021-02-28T12:47:00"};
        cmd.parseArgs(args);
        assertEquals("--", sc.getQueryParams().getParam(LOCATION));
        assertEquals("https://"+hostname + ":443/fdsnws/dataselect/1/query?channel=HHZ&endtime=2021-02-28T12:47:00.999&location=--&network=CO&starttime=2021-02-28T12:45:00.000&station=JSC",
                sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testDashDashInListLocCode() throws Exception {
        String hostname = "service.earthscope.org";
        FDSNDataSelectClient sc = new FDSNDataSelectClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"-n", "CO", "-s", "JSC", "-c", "HHZ", "-l", "--,00", "-b", "2021-02-28T12:45:00", "-e", "2021-02-28T12:47:00"};
        cmd.parseArgs(args);
        assertEquals("--,00", sc.getQueryParams().getParam(LOCATION));
        assertEquals("https://"+hostname + ":443/fdsnws/dataselect/1/query?channel=HHZ&endtime=2021-02-28T12:47:00.999&location=--,00&network=CO&starttime=2021-02-28T12:45:00.000&station=JSC",
                sc.getQueryParams().formURI().toString());
    }
}
