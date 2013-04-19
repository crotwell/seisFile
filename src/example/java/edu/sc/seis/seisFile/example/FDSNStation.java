package edu.sc.seis.seisFile.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;


public class FDSNStation {

    public void run() {
        try {
            FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryParams.area(30, 35, -83, -79)
                    .setStartTime(sdf.parse("2001-03-15"))
                    .setEndTime(sdf.parse("2010-03-21"))
                    .appendToNetwork("CO")
                    .setLevel(FDSNStationQueryParams.LEVEL_STATION);
            URL url = queryParams.formURI().toURL();
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn.getResponseCode() == 204) {
                System.out.println("No Data");
            } else if (conn.getResponseCode() == 200) {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLEventReader r = factory.createXMLEventReader(url.toString(), conn.getInputStream());
                FDSNStationXML xml = new FDSNStationXML(r);
                if (!xml.checkSchemaVersion()) {
                    System.out.println("");
                    System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                    System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                    System.out.println("XmlSchema (doc): " + xml.getSchemaVersion());
                }
                NetworkIterator nIt = xml.getNetworks();
                while (nIt.hasNext()) {
                    Network n = nIt.next();
                    System.out.println("Network: "+n.getCode()+"  "+n.getDescription());
                    StationIterator sIt = n.getStations();
                    while(sIt.hasNext()) {
                        Station s = sIt.next();
                        System.out.println(s.getLatitude()+"/"+s.getLongitude()+" "+s.getCode()+" "+s.getSite().getName()+" "+s.getStartDate());
                    }
                }
            } else {
                System.err.println("oops, error Response Code :" + conn.getResponseCode());
                String out = "";
                BufferedReader errReader = null;
                try {
                    errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    for (String line; (line = errReader.readLine()) != null;) {
                        out += line + "\n";
                    }
                } finally {
                    if (errReader != null)
                        try {
                            errReader.close();
                            conn.disconnect();
                        } catch(IOException e) {
                            throw e;
                        }
                }
                System.err.println("Error in connection with url: " + url);
                System.err.println(out);
            }
        } catch(Exception e) {
            System.err.println("Oops: " + e.getMessage());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FDSNStation ee = new FDSNStation();
        ee.run();
    }
}
