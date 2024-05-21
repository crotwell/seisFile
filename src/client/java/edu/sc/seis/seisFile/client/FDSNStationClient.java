package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;
import edu.sc.seis.seisFile.fdsnws.stationxml.BaseNodeType;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.DataAvailability;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="fdsnstation",
         description="example client to query a remote FDSN Station web service",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class FDSNStationClient extends AbstractFDSNClient {

    FDSNStationQueryParams queryParams;

    @Mixin
    FDSNStationCmdLineQueryParams cmdLine;

    @Option(names= { "--schema"}, description="prints schema")
    public boolean isPrintSchema = false;
    
    public FDSNStationClient() {
        this.cmdLine = new FDSNStationCmdLineQueryParams();
        this.queryParams = this.cmdLine.queryParams;
    }
    
    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else if (isPrintSchema) {
                PrintStream out = System.out;
                if (outputFile != null) {
                    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                }
                FDSNStationXML.printSchema(out);
                out.println();
                out.flush();
                return 0;
            } else {
                FDSNStationQuerier querier = new FDSNStationQuerier(queryParams);
                if (isValidate) {
                    querier.validateFDSNStationXML();
                    System.out.println("Valid");
                } else if (isRaw) {
                    PrintStream out = System.out;
                    if (outputFile != null) {
                        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                    }
                    querier.outputRaw(out);
                    out.println();
                    out.flush();
                } else {
                    FDSNStationXML stationXml = querier.getFDSNStationXML();
                    if (!stationXml.checkSchemaVersion()) {
                        System.out.println();
                        System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                        System.out.println("XmlSchema (code): " + StationXMLTagNames.CURRENT_SCHEMA_VERSION);
                        System.out.println("XmlSchema (doc): " + stationXml.getSchemaVersion());
                    }
                    handleResults(stationXml);
                    stationXml.closeReader();
                }
            }
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public void handleResults(FDSNStationXML stationXml) throws XMLStreamException, SeisFileException {
        NetworkIterator nIt = stationXml.getNetworks();
        while (nIt.hasNext()) {
            Network n = nIt.next();
            System.out.println(n.getCode()+" "+n.getStartDate() + " " + n.getDescription()+" "+extractDataAvailability(n));
            StationIterator sIt = n.getStations();
            while (sIt.hasNext()) {
                Station s = sIt.next();
                System.out.println("    "+n.getCode() + "." + s.getCode() + " " + s.getLatitude() + "/" + s.getLongitude()
                        + " " + s.getSite() + " " + s.getStartDate()+" "+extractDataAvailability(s));
                List<Channel> chanList = s.getChannelList();
                for (Channel channel : chanList) {
                    System.out.println("        " + channel.getLocCode() + "." + channel.getCode() + " "
                            + channel.getAzimuth() + "/" + channel.getDip() + " " + channel.getDepth().getValue() + " "
                            + channel.getDepth().getUnit() + " " + channel.getStartDate()+" "+extractDataAvailability(channel));
                    
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

    public FDSNStationQueryParams getQueryParams() {
        return queryParams;
    }

    protected void internalSetBaseURI(URI uri) {
        queryParams.setBaseURL(uri);
    }
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new FDSNStationClient()).execute(args));
    }
}
