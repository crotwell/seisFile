package edu.sc.seis.seisFile.example;

import edu.sc.seis.seisFile.seedlink.SeedlinkState;
import edu.sc.seis.seisFile.seedlink.SeedlinkPacket;
import edu.sc.seis.seisFile.seedlink.SeedlinkReader;
import org.json.JSONObject;

import java.util.*;

public class SeedLinkResume {

    public static void main(String[] args) throws Exception {
        SeedlinkReader reader = new SeedlinkReader( true);
        String[] lines = reader.sendHello();
        List<String> locChan = Arrays.asList("HHZ", "00HHZ", "BHZ", "00BHZ");
        reader.selectData("CO", "BIRD", locChan);
        reader.selectData("US", "NHSC", locChan);
        // try with some channel wildcards
        List<String> wcLocChan = Arrays.asList("??HHZ", "00?HZ");
        reader.selectData("CO", "JSC", wcLocChan);
        reader.endHandshake();
        SeedlinkState state = reader.getState();
        int n=0;
        while (n<20 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
        }
        reader.close();

        // now resume at the last seq number (assume global, not per station)
        // this was updated automatically in state by SeedlinkReader
        String lastSeq = state.getGlobalLastSequence();
        String host = state.getHost();
        int port = state.getPort();
        List<String> cmdList = state.getCommandList();
        // create new state as if load from file
        state = new SeedlinkState(host, port, cmdList);
        state.updateGlobalSequence(lastSeq);
        reader = SeedlinkReader.resumeGlobalSequence(state, true);
        System.out.println("Resume at global last sequence number: "+lastSeq);
        reader.endHandshake();

        n=0;
        while (n<25 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
            // update state so can resume later with per station sequence numbers
            // this must be done manually by the client if desired
            state.update(slp);
        }
        reader.close();

        // maybe save state to/from a file? Here just print to stdout
        System.out.println("Seedlink State:");
        JSONObject savedState = state.exportToJson();
        System.out.println(savedState.toString(2));


        // now resume with per station seq numbers, assume no wildcards used in STATION commands
        System.out.println("Resume at per station last sequence number");
        state = SeedlinkState.importFromJson(savedState);
        for(String k : state.getMap().keySet()) {
            System.out.println(k+"  "+state.getMap().get(k));
        }
        reader = SeedlinkReader.resume(state, true);
        // possibly send extra commands before...
        reader.endHandshake();
        n=0;
        while (n<20 && reader.hasNext()) {
            SeedlinkPacket slp = reader.readPacket();
            n++;
            System.out.println(slp.getMiniSeed().getHeader().getStationIdentifier()+", "+slp.getMiniSeed().getHeader().getChannelIdentifier()+", "+slp.getMiniSeed().getHeader().getLocationIdentifier());
            state.update(slp);
        }
        reader.close();
        System.out.println("Seedlink State:");
        savedState = state.exportToJson();
        System.out.println(savedState.toString(2));
    }
}
