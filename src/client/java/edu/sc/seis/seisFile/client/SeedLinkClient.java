package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.seedlink.SeedlinkPacket;
import edu.sc.seis.seisFile.seedlink.SeedlinkReader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

/**
 * Added support for an info output file and specifying a start and end time.
 */
@Command(name="seedlinkclient", 
         description="Example client to stream miniseed over seedlink.", 
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class SeedLinkClient extends AbstractClient {

    @Option(names= {"-h", "--host"}, description="host to connect to, defaults to IRIS, "+SeedlinkReader.DEFAULT_HOST)
    public String host = SeedlinkReader.DEFAULT_HOST;
    
    @Option(names= {"-p", "--port"}, description="port to connect to, defaults to IRIS, ${DEFAULT-VALUE}", defaultValue=""+SeedlinkReader.DEFAULT_PORT)
    public Integer port = SeedlinkReader.DEFAULT_PORT;
    
    @Option(names= {"-n", "--network"}, description="list of networks to search", defaultValue = "*", split = ",")
    List<String> network = new ArrayList<>();
    @Option(names= {"-s", "--station"}, description="list of stations to search", defaultValue = "*", split = ",")
    List<String> station = new ArrayList<>();
    @Option(names= {"-l", "--location"}, description="list of locations to search", defaultValue = "  ,??", split = ",")
    List<String> location = new ArrayList<>();
    @Option(names= {"-c", "--channel"}, description="list of channels to search", defaultValue = "???", split = ",")
    List<String> channel = new ArrayList<>();
    
    @Option(names = { "-b","--starttime","--start" }, description="Limit results to time series samples on or after the specified start time", converter=FloorISOTimeParser.class)
    Instant start;
    @Option(names = { "-e","--endtime","--end" }, description="Limit results to time series samples on or before the specified end time", converter=CeilingISOTimeParser.class)
    Instant end;

    
    @Option(names= {"--itype"}, description="info typ, ex "+SeedlinkReader.INFO_STREAMS)
    String infoType = SeedlinkReader.EMPTY;
    @Option(names= { "--iout"}, description="info out file")
    String ioutFile = SeedlinkReader.EMPTY;
    
    @Option(names= { "--max"}, description="number of packets to receive before ending the connection, defaults to ${DEFAULT-VALUE}", defaultValue="10")
    public int maxRecords = 10;

    @Option(names={"-o","--output"}, description = "Output file (default: print to console)")
    private File outputFile = null;

    @Option(names= {"--timeout"}, description="timeout seconds, defaults to ${DEFAULT-VALUE}", defaultValue = ""+SeedlinkReader.DEFAULT_TIMEOUT_SECOND)
    public Integer timeoutSec = SeedlinkReader.DEFAULT_TIMEOUT_SECOND;
    
    
    public SeedLinkClient() {
    }
    
    @Override
    public Integer call() throws Exception {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        DataOutputStream dos = null;
        PrintWriter out = new PrintWriter(System.out, true);
        if (maxRecords < -1) {
            maxRecords = -1;
        }
        if (outputFile != null) {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        }
        SeedlinkReader reader = new SeedlinkReader(host, port, timeoutSec, verbose);
        reader.setVerboseWriter(out);
        try {
            if (verbose) {
                String[] lines = reader.sendHello();
                for (String l : lines) {
                    out.println(l);
                }
            }
            if (!infoType.isEmpty()) {
                String infoString = reader.getInfoString(infoType);
                if (ioutFile == null || ioutFile.isEmpty()) {
                    out.println(infoString);
                } else {
                    PrintWriter pw = null;
                    try {
                        pw = new PrintWriter(ioutFile);
                        pw.print(infoString);
                    }
                    finally {
                        if (pw != null) {
                            pw.println();
                            pw.close();
                        }
                    }
                }
            } else if (maxRecords != 0) {
                ArrayList<String> locChanList = new ArrayList<>();
                for (String loc : location) {
                    for (String chan : channel) {
                        locChanList.add(loc + chan);
                    }
                }
                for (String n : network) {
                    for (String s : station) {
                        if (start == null) {
                            reader.selectData(n, s, locChanList);
                        } else if (end == null) {
                            reader.selectTime(n, s, locChanList, start);
                        } else {
                            reader.selectTime(n, s, locChanList, start, end);
                        }
                    }
                }
                reader.endHandshake();
                int i = 0;
                try {
                    System.out.println(maxRecords+" "+((maxRecords == -1 || i < maxRecords) && reader.hasNext()));
                    while ((maxRecords == -1 || i < maxRecords) && reader.hasNext()) {
                        SeedlinkPacket slp = reader.readPacket();
                        DataRecord dr = slp.getMiniSeed();
                        if (dos != null) {
                            dr.write(dos);
                        }
                        if (dos == null || verbose) {
                            // print something to the screen if we are not saving to
                            // disk
                            out.println(dr.oneLineSummary());
                            out.flush();
                        }
                        i++;
                    }
                } catch(EOFException e) {
                    // done I guess
                    if (verbose) {
                        out.println("Caught EOFException...");
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (dos != null) {
                dos.close();
            }
            reader.close();
            out.println("Finished: " + Instant.now());
        }
        return 0;
    }
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new SeedLinkClient()).execute(args));
    }

}
