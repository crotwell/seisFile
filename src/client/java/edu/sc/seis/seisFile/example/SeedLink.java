package edu.sc.seis.seisFile.example;

import edu.sc.seis.seisFile.seedlink.PerStationLastSequence;
import edu.sc.seis.seisFile.seedlink.SeedlinkPacket;
import edu.sc.seis.seisFile.seedlink.SeedlinkReader;

import java.util.*;

public class SeedLink {

    public static void main(String[] args) throws Exception {
        SeedlinkReader reader = new SeedlinkReader( true);
        String[] lines = reader.sendHello();
        List<String> locChan = Arrays.asList("HHZ", "00HHZ", "HHN", "00HHN", "HHE", "00HHE");
        reader.selectData("CO", "BIRD", locChan);
        reader.selectData("US", "NHSC", locChan);
        reader.selectData("CO", "JSC", new ArrayList<String>());
        reader.endHandshake();
        int n=0;
        while (n<10 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
        }
        reader.close();
        // now resume at the last seq number (assume global not per station)
        String lastSeq = reader.lastSeqNum;
        System.out.println("Resume at global last sequence number: "+lastSeq);
        reader.resume();

        PerStationLastSequence perStationSeq = new PerStationLastSequence();
        while (n<15 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
            perStationSeq.update(slp);
        }
        reader.close();

        // now resume with per station seq numbers
        System.out.println("Resume at per station last sequence number");
        for(String k : perStationSeq.getMap().keySet()) {
            System.out.println(k+"  "+perStationSeq.getMap().get(k));
        }
        reader.resume(perStationSeq);
        while (n<20 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
            perStationSeq.update(slp);
        }
        reader.close();
    }
}
