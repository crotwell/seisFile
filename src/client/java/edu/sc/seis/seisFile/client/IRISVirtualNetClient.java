package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQuerier;
import edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQueryParams;
import edu.sc.seis.seisFile.fdsnws.stationxml.BaseNodeType;
import edu.sc.seis.seisFile.fdsnws.stationxml.DataAvailability;
import edu.sc.seis.seisFile.fdsnws.virtualnet.ContributorNetwork;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualNetwork;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualNetworkList;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualStation;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="irisvirtnet",
         description="example client to query the IRIS Virtual Network web service",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class IRISVirtualNetClient extends AbstractClient {

    IRISWSVirtualNetworkQueryParams queryParams;

    @Mixin
    IRISVirtualNetworkCmdLineQueryParams cmdLine;

    
    public IRISVirtualNetClient() {
        this.cmdLine = new IRISVirtualNetworkCmdLineQueryParams();
        this.queryParams = this.cmdLine.queryParams;
    }


    @Option(names="--"+AbstractFDSNClient.PRINTURL, description="Construct and print URL and exit")
    public boolean isPrintUrl = false;
    
    
    @Option(names="--"+AbstractFDSNClient.RAW, description="Output the raw data to stdout")
    public boolean isRaw = false;

    
    @Option(names={"-o","--output"}, description="File for outputing result")
    public File outputFile;
    
    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        PrintStream out = System.out;
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else {
                IRISWSVirtualNetworkQuerier querier = new IRISWSVirtualNetworkQuerier(queryParams);
                if (isRaw) {
                    if (outputFile != null) {
                        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                    }
                    if (outputFile != null) {
                        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                    }
                    querier.outputRaw(out);
                    out.println();
                    out.flush();
                } else {
                    VirtualNetworkList virtNetList = querier.getVirtual();
                    handleResults(virtNetList);
                }
            }
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        } finally {
            if (outputFile != null && out != null) {
                out.close();
            }
        }
        return 0;
    }

    public void handleResults(VirtualNetworkList virtNetList) {
        PrintStream out = System.out;
        for (VirtualNetwork vnet:virtNetList.getVirtualNetworks()) {
            out.println(vnet.getCode()+" "+vnet.getStart() + " " + vnet.getDescription());
            out.println("Def:"+ vnet.getDefinition()!=null ? vnet.getDefinition() : "");
            for (ContributorNetwork cnet: vnet.getContribNetList()) {
                for (VirtualStation s : cnet.getStationList()) {
                out.println("    "+cnet.getCode()+ "." + s.getCode() + " " + s.getVnetStart() + " " + s.getPrimaryDC());
                
                }
            }
        }
    }
    
    String extractDataAvailability(BaseNodeType node) {
        DataAvailability da = node.getDataAvailability();
        if (da != null && da.getExtent() != null) {
            return " ("+da.getExtent().getStart()+" to "+da.getExtent().getEnd()+") ";
        }
        return "";
    }

    public IRISWSVirtualNetworkQueryParams getQueryParams() {
        return queryParams;
    }

    protected void internalSetBaseURI(URI uri) {
        queryParams.setBaseURL(uri);
    }
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new IRISVirtualNetClient()).execute(args));
    }
}
