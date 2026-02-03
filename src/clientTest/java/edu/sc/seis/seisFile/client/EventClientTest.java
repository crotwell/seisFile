package edu.sc.seis.seisFile.client;

import edu.sc.seis.seisFile.fdsnws.FDSNEventQueryParams;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventClientTest {

    @Test
    public void testDefaultHostArg() throws Exception {
        String hostname = FDSNEventQueryParams.USGS_HOST;
        FDSNEventClient sc = new FDSNEventClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"--minmag", "8.0"};
        cmd.parseArgs(args);
        assertEquals("http://"+hostname + ":80/fdsnws/event/1/query?minmagnitude=8.0", sc.getQueryParams().formURI().toString());
    }

    @Test
    public void testHostArg() throws Exception {
        String hostname = "service.seis.sc.edu";
        FDSNEventClient sc = new FDSNEventClient(); // the instance to populate
        CommandLine cmd = new CommandLine(sc);
        String[] args = new String[] {"--host", hostname, "--minmag", "8.0"};
        cmd.parseArgs(args);
        assertEquals("http://"+hostname + ":80/fdsnws/event/1/query?minmagnitude=8.0", sc.getQueryParams().formURI().toString());
    }

}
