package edu.sc.seis.seisFile.fdsnws;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.client.BoxAreaParser;
import edu.sc.seis.seisFile.client.DonutParser;
import edu.sc.seis.seisFile.client.ISOTimeParser;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;


public class StationClient extends AbstractFDSNClient {

    public StationClient(String[] args) throws JSAPException {
        super(args);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void addParams() throws JSAPException {
        super.addParams();
        add(BoxAreaParser.createParam("Event constraining box as west/east/south/north"));
        add(DonutParser.createParam("Event constraining donut as lat/lon/minRadius/maxRadius"));
        add(ISOTimeParser.createParam(BEGIN, "The earliest time for acceptance", false));
        add(ISOTimeParser.createParam(END, "The latest time for acceptance", true));
        /*
        add(ISOTimeParser.createYesterdayParam(FDSNStationQueryParams.STARTBEFORE, "The level must have started by this time", false));
        add(ISOTimeParser.createParam(FDSNStationQueryParams.ENDBEFORE, "now", "The level must have ended by this time", true));
        add(ISOTimeParser.createYesterdayParam(FDSNStationQueryParams.STARTAFTER, "The level must have started after this time", false));
        add(ISOTimeParser.createParam(FDSNStationQueryParams.ENDAFTER, "now", "The level must have ended after this time", true));
*/
        add(createListOption(FDSNStationQueryParams.NETWORK, 'n', FDSNStationQueryParams.NETWORK, "A comma separated list of networks to search"));
        add(createListOption(FDSNStationQueryParams.STATION, 's', FDSNStationQueryParams.STATION, "A comma separated list of stations to search"));
        add(createListOption(FDSNStationQueryParams.LOCATION, 'l', FDSNStationQueryParams.LOCATION, "A comma separated list of locations to search"));
        add(createListOption(FDSNStationQueryParams.CHANNEL, 'c', FDSNStationQueryParams.CHANNEL, "A comma separated list of channels to search"));
        add(LevelParser.createFlaggedOption());
        add(new Switch(FDSNStationQueryParams.INCLUDEAVAILABILITY, JSAP.NO_SHORTFLAG, "availability" , "include information about time series data availability"));
        add(new Switch(FDSNStationQueryParams.INCLUDERESTRICTED, JSAP.NO_SHORTFLAG, "restricted" , "include information for restricted stations"));
        add(ISOTimeParser.createParam(FDSNStationQueryParams.UPDATEDAFTER, "Only results that have changed since the date are accepted", false));
    }

    public void run() {
        FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
        JSAPResult result = getResult();
        if (!isSuccess()) {
            for (Iterator errs = result.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println();
            System.err.println("Usage: java " + this.getClass().getName());
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            return;
        }
        if (shouldPrintHelp()) {
            System.out.println(jsap.getHelp());
            return;
        }
        if (result.contains(BoxAreaParser.NAME)) {
            HashMap<String, String> box = (HashMap<String, String>)result.getObject(BoxAreaParser.NAME);
            queryParams.area(Float.parseFloat(box.get("west")),
                             Float.parseFloat(box.get("east")),
                             Float.parseFloat(box.get("south")),
                             Float.parseFloat(box.get("north")));
        }
        if (result.contains(DonutParser.NAME)) {
            HashMap<String, String> donut = (HashMap<String, String>)result.getObject(DonutParser.NAME);
            queryParams.donut(Float.parseFloat(donut.get("lat")),
                             Float.parseFloat(donut.get("lon")),
                             Float.parseFloat(donut.get("min")),
                             Float.parseFloat(donut.get("max")));
        }
        if (result.contains(FDSNStationQueryParams.UPDATEDAFTER)) {
            queryParams.setUpdatedAfter((Date)result.getObject(FDSNStationQueryParams.UPDATEDAFTER));
        }
        if (result.contains(BEGIN)) {
            queryParams.setEndAfter((Date)result.getObject(BEGIN));
        }
        if (result.contains(END)) {
            queryParams.setStartBefore((Date)result.getObject(END));
        }
        if (result.contains(FDSNStationQueryParams.NETWORK)) {
            String[] vals = result.getStringArray(FDSNStationQueryParams.NETWORK);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToNetwork(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.STATION)) {
            queryParams.appendToStation(result.getString(FDSNStationQueryParams.STATION));
            String[] vals = result.getStringArray(FDSNStationQueryParams.STATION);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToStation(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.LOCATION)) {
            queryParams.appendToLocation(result.getString(FDSNStationQueryParams.LOCATION));
            String[] vals = result.getStringArray(FDSNStationQueryParams.LOCATION);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToLocation(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.CHANNEL)) {
            queryParams.appendToChannel(result.getString(FDSNStationQueryParams.CHANNEL));
            String[] vals = result.getStringArray(FDSNStationQueryParams.CHANNEL);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToChannel(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.LEVEL)) {
            queryParams.setLevel(result.getString(FDSNStationQueryParams.LEVEL));
        }
        if (result.getBoolean(FDSNStationQueryParams.INCLUDEAVAILABILITY)) {
            queryParams.setIncludeAvailability(true);
        }
        if (result.getBoolean(FDSNStationQueryParams.INCLUDERESTRICTED)) {
            queryParams.setIncludeRestricted(true);
        }
        try {
            if (getResult().getBoolean(PRINTURL)) {
                System.out.println(queryParams.formURI());
                return;
            }
            process(queryParams.formURI());
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(SeisFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void process(URI uri) throws IOException, XMLStreamException, SeisFileException {
        URL url = uri.toURL();
        connect(uri);
        if (! isError()) {
            if (! isEmpty()) {
                FDSNStationXML stationXml = new FDSNStationXML(getReader());
                if (!stationXml.checkSchemaVersion()) {
                    System.out.println("");
                    System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                    System.out.println("XmlSchema (code): " + StationXMLTagNames.CURRENT_SCHEMA_VERSION);
                    System.out.println("XmlSchema (doc): " + stationXml.getSchemaVersion());
                }
                handleResults(stationXml);
            } else {
                System.out.println("No Data");
            }
        } else {
            System.err.println("Error: "+getErrorMessage());
        }
    }

    public void handleResults(FDSNStationXML stationXml) throws XMLStreamException, SeisFileException {
        NetworkIterator nIt = stationXml.getNetworks();
        while (nIt.hasNext()) {
            Network n = nIt.next();
            StationIterator sIt = n.getStations();
            while(sIt.hasNext()) {
                Station s = sIt.next();
                System.out.println(n.getCode()+"."+s.getCode()+" "+s.getLatitude() + "/" + s.getLongitude() + " " + s.getName() + " "
                        + s.getSite() + " " + s.getStartDate());
                List<Channel> chanList= s.getChannelList();
                for (Channel channel : chanList) {
                    System.out.println("        "+channel.getLocCode()+"."+channel.getCode()+" "+channel.getAzimuth()+"/"+channel.getDip()+" "+channel.getDepth().getValue()+" "+channel.getDepth().getUnit()+" "+channel.getStartDate());
                }
            }
        }
    }

    /**
     * @param args
     * @throws JSAPException
     */
    public static void main(String[] args) throws JSAPException {
        StationClient sc = new StationClient(args);
        sc.run();
    }
}
