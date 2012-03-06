package edu.sc.seis.seisFile.winston;

public class WinstonSCNL {

    protected WinstonSCNL(String databaseName, String prefix) {
        this.prefix = prefix;
        String woPrefix = databaseName.substring(prefix.length()+1);
        String[] s = woPrefix.split("\\$");
        if (s.length == 4) {
            this.locId = s[3];
        } else if (s.length != 3) {
            throw new IllegalArgumentException("Invalid Winston database name: '"+databaseName+"', must have 2 or 3 $'s");
        } else {
            this.locId = null;
        }
        this.station = s[0];
        this.channel = s[1];
        this.network = s[2];
    }
    
    protected WinstonSCNL(String station, String channel, String network, String locId, String prefix) {
        super();
        this.station = station;
        this.channel = channel;
        this.network = network;
        this.locId = locId;
        this.prefix = prefix;
    }
    
    public String getDatabaseName() {
        return WinstonUtil.prefixTableName(prefix, concatSCNL());
    }
    
    public String concatSCNL() {
        return station+SEP+channel+SEP+network+(locId!=null?SEP+locId:"");
    }

    public String getStation() {
        return station;
    }

    public String getChannel() {
        return channel;
    }

    public String getNetwork() {
        return network;
    }

    public String getLocId() {
        return locId;
    }
    
    static final String SEP = "$";

    String station;

    String channel;

    String network;

    String locId;
    
    String prefix;
}
