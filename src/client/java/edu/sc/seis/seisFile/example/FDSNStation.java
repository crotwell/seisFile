package edu.sc.seis.seisFile.example;

import java.util.List;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;

public class FDSNStation {

    public void run() {
        FDSNStationXML xml = null;
        try {
            FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
            queryParams.area(30, 35, -83, -79)
                    .setStartTime(TimeUtils.parseISOString("2010-03-15"))
                    .setEndTime(TimeUtils.parseISOString("2013-03-21"))
                    .appendToNetwork("CO")
                    .appendToChannel("?HZ")
                    .setLevel(FDSNStationQueryParams.LEVEL_CHANNEL);
            FDSNStationQuerier querier = new FDSNStationQuerier(queryParams);
            xml = querier.getFDSNStationXML();
            if (!xml.checkSchemaVersion()) {
                System.out.println();
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
                    System.out.println(s.getLatitude() + "/" + s.getLongitude() + " " + s.getCode() + " "
                            + s.getSite().getName() + " " + s.getStartDate());
                    List<Channel> chanList = s.getChannelList();
                    for (Channel channel : chanList) {
                        System.out.println("        " + channel.getLocCode() + "." + channel.getCode() + " "
                                + channel.getAzimuth() + "/" + channel.getDip() + " " + channel.getDepth().getValue()
                                + " " + channel.getDepth().getUnit() + " " + channel.getStartDate());
                    }
                }
            }
            xml.closeReader();
        } catch(Exception e) {
            System.err.println("Oops: " + e.getMessage());
        } finally {
            if (xml != null) {
                xml.closeReader();
                xml = null;
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new FDSNStation().run();
    }
}
