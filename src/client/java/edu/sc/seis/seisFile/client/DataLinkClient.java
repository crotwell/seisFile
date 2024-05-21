package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.datalink.DataLink;
import edu.sc.seis.seisFile.datalink.DataLinkException;
import edu.sc.seis.seisFile.datalink.DataLinkPacket;
import edu.sc.seis.seisFile.datalink.DataLinkResponse;
import edu.sc.seis.seisFile.mseed.DataRecord;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="datalinkclient", 
         description="Example client to stream miniseed over datalink.", 
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class DataLinkClient extends AbstractClient {

    @Option(names= {"-h", "--host"}, description="host to connect to, defaults to IRIS, ${DEFAULT-VALUE}", defaultValue=DataLink.IRIS_HOST)
    public String host = DataLink.IRIS_HOST;
    
    @Option(names= {"-p", "--port"}, description="port to connect to, defaults to IRIS, ${DEFAULT-VALUE}", defaultValue=""+DataLink.IRIS_PORT)
    public Integer port = DataLink.IRIS_PORT;
    
    @Option(names= {"-m", "--match"}, description="match pattern, as a regular expression. For miniseed the conventions is NN_SSS_LL_CCC/MSEED")
    public String match = "CO_BIRD_00_HHZ";
    
    @Option(names= { "--max"}, description="number of packets to receive before ending the connection, defaults to ${DEFAULT-VALUE}", defaultValue="10")
    public int maxRecords = 10;

    @Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    private File outputFile = null;
    
    @Option(names= {"--timeout"}, description="timeout seconds, defaults to ${DEFAULT-VALUE}", defaultValue = ""+DataLink.DEFAULT_TIMEOUT_SECOND)
    public Integer timeoutSec = DataLink.DEFAULT_TIMEOUT_SECOND;
    
    public DataLinkClient() {
        
    }

    @Override
    public Integer call() throws IOException, DataLinkException {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        DataLink dl = new DataLink(host, port, timeoutSec, verbose);
        try {
            System.out.println("Server ID: "+dl.getServerId());
            dl.match(match);
            dl.stream();
            if (outputFile != null) {
                dataout = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            }
            DataLinkResponse response;
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
            while (response instanceof DataLinkPacket) {
                handlePacket((DataLinkPacket)response);
                response = dl.readPacket();
            }
            if ( ! response.getKey().equals(DataLink.ENDSTREAM)) {
                System.err.println("Expected ENDSTREAM, but got "+response.getKey());
            }
        } finally {
            dl.close();
            if (dataout != null) {
                dataout.close();
                dataout = null;
            }
        }
        return 0;
    }
   
    
    public void handlePacket(DataLinkPacket packet) throws DataLinkException, IOException {
        if (dataout != null) {
            dataout.write(packet.getRawData());
        }
        if (dataout == null || verbose ){
            if (packet.isMiniseed()) {
                DataRecord dr = packet.getMiniseed();
                System.out.println(dr.oneLineSummary());
            } else {
                System.out.println(" Packet: "+packet.getStreamId()+"  "+packet.getHppacketstart());
            }
        }
    }
    
    private DataOutputStream dataout;

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new DataLinkClient()).execute(args));
    }
    
}
