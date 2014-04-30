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
        return station+SEP+channel+SEP+network+((locId!=null && locId.trim().length()!=0)?SEP+locId:"");
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
    
    public String toString() {
        return getDatabaseName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((locId == null) ? 0 : locId.hashCode());
        result = prime * result + ((network == null) ? 0 : network.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((station == null) ? 0 : station.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WinstonSCNL other = (WinstonSCNL)obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        } else if (!channel.equals(other.channel))
            return false;
        if (locId == null) {
            if (other.locId != null)
                return false;
        } else if (!locId.equals(other.locId))
            return false;
        if (network == null) {
            if (other.network != null)
                return false;
        } else if (!network.equals(other.network))
            return false;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        if (station == null) {
            if (other.station != null)
                return false;
        } else if (!station.equals(other.station))
            return false;
        return true;
    }

    static final String SEP = "$";

    String station;

    String channel;

    String network;

    String locId;
    
    String prefix;
    
    
}
