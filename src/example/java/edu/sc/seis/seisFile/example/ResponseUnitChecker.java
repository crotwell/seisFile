package edu.sc.seis.seisFile.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.fdsnws.FDSNStationQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.ResponseStage;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;

public class ResponseUnitChecker {

    PrintWriter unitOut;

    PrintWriter unitChan;

    public ResponseUnitChecker() throws IOException {
        unitOut = new PrintWriter(new BufferedWriter(new FileWriter("units.out", true)));
        unitChan = new PrintWriter(new BufferedWriter(new FileWriter("units.channel", true)));
    }

    public void run(String[] args) {
        runNetwork(args[0]);
    }

    public void runNetwork(String net) {
        try {
            FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryParams.appendToNetwork(net)
                    .setLevel(FDSNStationQueryParams.LEVEL_RESPONSE);
            FDSNStationQuerier querier = new FDSNStationQuerier(queryParams);
            FDSNStationXML xml = querier.getFDSNStationXML();
            if (!xml.checkSchemaVersion()) {
                System.out.println("");
                System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                System.out.println("XmlSchema (doc): " + xml.getSchemaVersion());
            }
            NetworkIterator nIt = xml.getNetworks();
            while (nIt.hasNext()) {
                Network n = nIt.next();
                System.out.println("Network: " + n.getCode() + "  " + n.getDescription());
                StationIterator sIt = n.getStations();
                while (sIt.hasNext()) {
                    Station s = sIt.next();
                    System.out.println(s.getCode() + " " + s.getSite().getName() + " " + s.getStartDate());
                    List<Channel> chanList = s.getChannelList();
                    System.out.print("        ");
                    for (Channel channel : chanList) {
                        System.out.print(channel.getLocCode() + "." + channel.getCode() + " ");
                        if (channel.getCalibrationUnits() != null) {
                            printUnit(channel.getCalibrationUnits().getName(), channel.getCalibrationUnits()
                                    .getDescription(), channel);
                        }
                        List<ResponseStage> stageList = channel.getResponse().getResponseStageList();
                        for (ResponseStage responseStage : stageList) {
                            if (responseStage.getResponseItem() != null) {
                                if (responseStage.getResponseItem().getInputUnits() != null) {
                                    printUnit(responseStage.getResponseItem().getInputUnits().getName(),
                                              responseStage.getResponseItem().getInputUnits().getDescription(),
                                              channel);
                                }
                                if (responseStage.getResponseItem().getOutputUnits() != null) {
                                    printUnit(responseStage.getResponseItem().getOutputUnits().getName(),
                                              responseStage.getResponseItem().getOutputUnits().getDescription(),
                                              channel);
                                }
                            }
                        }
                    }
                    System.out.println();
                }
            }
            unitOut.flush();
            unitChan.flush();
        } catch(Exception e) {
            System.err.println("Oops: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void printUnit(String unit, String desc, Channel chan) {
        unitOut.println(unit + ", " + desc);
        unitChan.println(unit + ", " + desc + ", " + chan.getNetworkCode() + "." + chan.getStationCode() + "."
                + chan.getLocCode() + "." + chan.getCode() + ", " + chan.getStartDate());
    }

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        new ResponseUnitChecker().run(args);
    }
}
