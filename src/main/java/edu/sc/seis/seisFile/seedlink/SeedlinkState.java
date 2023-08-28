package edu.sc.seis.seisFile.seedlink;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class SeedlinkState {

    private String globalLastSequence  = "000000";

    private Map<String, String> lastSeq;

    private List<String> commandList;

    String host;

    int port;

    protected SeedlinkState() {
        this(null, 0, new HashMap<String, String>(), new ArrayList<String>());
    }
    public SeedlinkState(String host, int port, List<String> commandList) {
        this(host, port, new HashMap<String, String>(), commandList);
    }

    public SeedlinkState(String host, int port, Map<String, String> lastSeq, List<String> commandList) {
        this.host = host;
        this.port = port;
        this.commandList = commandList;
        this.lastSeq = lastSeq;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
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
        globalLastSequence = seq;
        lastSeq.put(netCode.trim()+"."+staCode.trim(), seq);
    }

    public String getForStation(String netCode, String staCode) {
        return lastSeq.get(netCode.trim()+"."+staCode.trim());
    }

    public String getGlobalLastSequence() {
        return globalLastSequence;
    }

    public void updateGlobalSequence(String seq) {
        this.globalLastSequence = seq;
    }

    public void update(SeedlinkPacket slp) throws SeedFormatException, IOException {
        put(slp.getMiniSeed().getHeader().getNetworkCode(),
                slp.getMiniSeed().getHeader().getStationIdentifier(),
                slp.getSeqNum());
    }

    public Map<String, String> getMap() {
        return lastSeq;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public JSONObject exportToJson() {
        JSONObject json = new JSONObject();
        json.put("host", getHost());
        json.put("port", getPort());
        json.put("globalSequence", globalLastSequence);
        JSONArray cmds = new JSONArray();
        for (String c : commandList) {
            cmds.put(c);
        }
        json.put("commands", cmds);
        JSONObject seqVals = new JSONObject();
        json.put("sequence", seqVals);
        for( String k : lastSeq.keySet()) {
            seqVals.put(k, lastSeq.get(k));
        }
        return json;
    }

    public static SeedlinkState importFromJson(JSONObject json) {
        SeedlinkState ls = new SeedlinkState();
        ls.host = json.getString("host");
        ls.port = json.getInt("port");
        ls.globalLastSequence = json.getString("globalSequence");
        JSONObject seqVals = json.getJSONObject("sequence");
        for (String k : seqVals.keySet()) {
            ls.lastSeq.put(k, seqVals.getString(k));
        }
        JSONArray cmdList = json.getJSONArray("commands");
        ls.commandList = new ArrayList<>();
        for(int i=0; i< cmdList.length(); i++) {
            ls.commandList.add(cmdList.getString(i));
        }
        return ls;
    }

}
