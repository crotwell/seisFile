package edu.sc.seis.seisFile.fdsnws;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
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
import picocli.CommandLine.Mixin;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

public class StationClient extends AbstractFDSNClient {

    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else {
                FDSNStationQuerier querier = new FDSNStationQuerier(queryParams);
                if (isValidate) {
                    querier.validateFDSNStationXML();
                    System.out.println("Valid");
                } else if (isRaw) {
                    querier.outputRaw(System.out);
                } else {
                    FDSNStationXML stationXml = querier.getFDSNStationXML();
                    if (!stationXml.checkSchemaVersion()) {
                        System.out.println("");
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

    @Mixin
    FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new StationClient()).execute(args));
    }
}
