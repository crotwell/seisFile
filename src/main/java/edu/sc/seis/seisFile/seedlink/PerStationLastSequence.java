package edu.sc.seis.seisFile.seedlink;

import edu.sc.seis.seisFile.mseed.SeedFormatException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PerStationLastSequence {
    private Map<String, String> lastSeq;

    public PerStationLastSequence() {
        this(new HashMap<String, String>());
    }

    public PerStationLastSequence(Map<String, String> lastSeq) {
        this.lastSeq = lastSeq;
    }

    public boolean contains(String netCode, String staCode) {
        return lastSeq.containsKey(netCode.trim()+"."+staCode.trim());
    }

    public boolean containsKey(String key) {
        return lastSeq.containsKey(key);
    }

    public Set<String> keySet() {
        return lastSeq.keySet();
    }

    public void put(String netCode, String staCode, String seq) {
        lastSeq.put(netCode.trim()+"."+staCode.trim(), seq);
    }

    public String getForStation(String netCode, String staCode) {
        return lastSeq.get(netCode.trim()+"."+staCode.trim());
    }

    public void update(SeedlinkPacket slp) throws SeedFormatException, IOException {
        put(slp.getMiniSeed().getHeader().getNetworkCode(),
                slp.getMiniSeed().getHeader().getStationIdentifier(),
                slp.getSeqNum());
    }

    public Map<String, String> getMap() {
        return lastSeq;
    }
}
