package edu.sc.seis.seisFile.seedlink;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, SeedlinkException, SeedFormatException {
        List<String> config = new ArrayList<String>();
        config.add("STATION * TA");
        config.add("SELECT BHZ.D");
        SeedlinkReader reader = new SeedlinkReader(config);
        int i=0;
        DataRecord firstdr = reader.next().getMiniSeed();
        Date firstDate = new Date();
        while(i<10000) {
            SeedlinkPacket slp = reader.next();
            DataRecord dr = slp.getMiniSeed();
            System.out.println(i+" Got a packet: "+slp.getSeqNum()+
                               "  "+ dr.getHeader().getNetworkCode()+
                               "  "+ dr.getHeader().getStationIdentifier()+
                               "  "+ dr.getHeader().getLocationIdentifier()+
                               "  "+ dr.getHeader().getChannelIdentifier()+
                               "  "+ dr.getHeader().getStartTime());
            i++;
        }
        DataRecord lastdr = reader.next().getMiniSeed();
        System.out.println(firstDate+"  "+firstdr.getHeader().getEndTime());
        System.out.println(new Date()+"  "+lastdr.getHeader().getEndTime());
        reader.close();
    }
}
