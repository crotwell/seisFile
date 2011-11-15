package edu.sc.seis.seisFile.stationxml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class StationXMLClient {

    public static void main(String[] args) throws XMLStreamException, StationXMLException, IOException {
        if (args.length != 2 || ! args[0].equals("-u")) {
            System.out.println("Usage: stationxmlclient -u url");
            System.out.println("       stationxmlclient -u http://www.iris.edu/ws/station/query?net=IU&sta=SNZO&chan=BHZ&level=chan");
            return;
        }
        URL url = new URL(args[1]);
        URLConnection urlConn = url.openConnection();
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection)urlConn;
            if (conn.getResponseCode() != 200) {
                String out = "";
                BufferedReader errReader = null;
                try {
                    errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    for (String line; (line = errReader.readLine()) != null;) {
                        out += line + "\n";
                    }
                } finally {
                    if (errReader != null) try { 
                        errReader.close(); 
                        conn.disconnect();
                    } catch (IOException e) {
                        throw e;
                    }
                }
                System.err.println("Error in connection with url: "+url);
                System.err.println(out);
                return;
            }
        }
        
        // likely not an error in the http layer, so assume XML is returned
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(url.toString(), urlConn.getInputStream());
        XMLEvent e = r.peek();
        while (!e.isStartElement()) {
            e = r.nextEvent(); // eat this one
            e = r.peek(); // peek at the next
        }
        System.out.println("StaMessage");
        StaMessage staMessage = new StaMessage(r);
        System.out.println("Source: " + staMessage.getSource());
        System.out.println("Sender: " + staMessage.getSender());
        System.out.println("Module: " + staMessage.getModule());
        System.out.println("SentDate: " + staMessage.getSentDate());
        NetworkIterator it = staMessage.getNetworks();
        while (it.hasNext()) {
            Network n = it.next();
            System.out.println("Network: " +n.getNetCode()+" "+n.getDescription()+" "+n.getStartDate()+" "+n.getEndDate()); 
            StationIterator sit = n.getStations();
            while (sit.hasNext()) {
                Station s = sit.next();
                if ( ! n.getNetCode().equals(s.getNetCode())) {
                    throw new StationXMLException("Station in wrong network: "+n.getNetCode()+" != "+s.getNetCode()+"  "+r.peek().getLocation());
                    
                }
                System.out.println("  Station: " + s.getNetCode() + "." + s.getStaCode() + " "
                        + s.getStationEpochs().size());
                List<StationEpoch> staEpochs = s.getStationEpochs();
                for (StationEpoch stationEpoch : staEpochs) {
                    System.out.println("    Station Epoch: " + s.getNetCode() + "." + s.getStaCode()
                                       + "  " + stationEpoch.getStartDate() + " to " + stationEpoch.getEndDate());
                    List<Channel> chanList = stationEpoch.getChannelList();
                    for (Channel channel : chanList) {
                        List<Epoch> chanEpochList = channel.getChanEpochList();
                        for (Epoch epoch : chanEpochList) {
                            System.out.println("      Channel Epoch: " + channel.getLocCode() + "." + channel.getChanCode()
                                    + "  " + epoch.getStartDate() + " to " + epoch.getEndDate());
                            float overallGain = 1;
                            float stageZeroGain = 1;
                            for (Response resp : epoch.getResponseList()) {
                                System.out.print("          Resp "+resp.getStage());
                                if (resp.getResponseItem() != null) {
                                    System.out.print(" "+resp.getResponseItem().getInputUnits()+" "+resp.getResponseItem().getOutputUnits());
                                }
                                if (resp.getStageSensitivity() != null) {
                                    System.out.print(" "+resp.getStageSensitivity().getSensitivityValue());
                                    if (resp.getStage() != 0) {
                                        overallGain *= resp.getStageSensitivity().getSensitivityValue();
                                    } else {
                                        stageZeroGain = resp.getStageSensitivity().getSensitivityValue();
                                    }
                                }
                                System.out.println();
                            }
                            System.out.println("          Overall Gain: "+overallGain);
                        }
                    }
                }
            }
        }
        staMessage.closeReader();
    }
}