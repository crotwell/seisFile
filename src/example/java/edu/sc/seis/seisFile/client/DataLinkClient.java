package edu.sc.seis.seisFile.client;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import edu.sc.seis.seisFile.datalink.DataLink;
import edu.sc.seis.seisFile.datalink.DataLinkException;
import edu.sc.seis.seisFile.datalink.DataLinkPacket;
import edu.sc.seis.seisFile.datalink.DataLinkResponse;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="datalinkclient", versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class DataLinkClient extends AbstractClient {

    @Option(names= {"-h", "--host"}, description="host to connect to, defaults to IRIS, "+DataLink.IRIS_HOST)
    public String host = DataLink.IRIS_HOST;
    
    @Option(names= {"-p", "--port"}, description="port to connect to, defaults to IRIS, "+DataLink.IRIS_PORT)
    public Integer port = DataLink.IRIS_PORT;
    
    @Option(names= {"-m", "--match"}, description="match pattern, as a regular expression. For miniseed the conventions is NN_SSS_LL_CCC/MSEED")
    public String match = "CO_BIRD_00_HHZ";
    
    @Option(names= { "--max"}, description="number of packets to receive before ending the connection")
    public int maxRecords = 10;

    @Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    private File outputFile;
    
    @Option(names= {"--timeout"}, description="timeout seconds")
    public Integer timeoutSec = 20;
    
    public DataLinkClient() {
        
    }

    @Override
    public Integer call() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, DataLinkException {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        run();
        return 0;
    }

    public void run() throws DataLinkException, IOException {
        DataLink dl = new DataLink(host, port, timeoutSec, verbose);
        System.out.println("Server ID: "+dl.getServerId());
        dl.match(match);
        dl.stream();
        DataLinkResponse response = null;
        for(int i=0; maxRecords==-1 || i<maxRecords; i++) {
            response = dl.readPacket();
            if (response instanceof DataLinkPacket) {
                handlePacket((DataLinkPacket)response);
            } else {
                System.out.println("Response: "+response.getKey());
            }
        }
        dl.endStream();
        response = dl.readPacket();
        while (response != null && response instanceof DataLinkPacket) {
            handlePacket((DataLinkPacket)response);
            response = dl.readPacket();
        }
        if ( ! response.getKey().equals(DataLink.ENDSTREAM)) {
            System.err.println("Expected ENDSTREAM, but got "+response.getKey());
        }
        dl.close();
    }
   
    
    public void handlePacket(DataLinkPacket packet) throws DataLinkException, IOException {
        if (dataout != null) {
            dataout.write(packet.getRawData());
        } else {
            System.out.println(" Packet: "+packet.getStreamId()+"  "+packet.getHppacketstart());
        }
    }
    
    private DataOutput dataout;

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new DataLinkClient()).execute(args));
    }
    
}
