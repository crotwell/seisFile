package edu.sc.seis.seisFile;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import edu.sc.seis.seisFile.fdsnws.AbstractQueryParams;

public class ChannelTimeWindow {

    public ChannelTimeWindow(String network,
                             String station,
                             String location,
                             String channel,
                             Instant beginTime,
                             Instant endTime) {
        super();
        this.network = network;
        this.station = station;
        this.location = location;
        this.channel = channel;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public ChannelTimeWindow(String network,
                             String station,
                             String location,
                             String channel,
                             Instant beginTime,
                             int durationSeconds) {
        this(network, station, location, channel, beginTime, beginTime.plusSeconds(durationSeconds));
    }

    public String getNetwork() {
        return network;
    }

    public String getStation() {
        return station;
    }

    public String getLocation() {
        return location;
    }

    public String getChannel() {
        return channel;
    }

    public Instant getBeginTime() {
        return beginTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String toString() {
        return formString(" ", AbstractQueryParams.createDateFormat(), false);
    }
    
    public String formString(String seperator, DateTimeFormatter df, boolean dashifyLocId) {
        String locId = location;
        if (dashifyLocId && ( "".equals(locId) || "  ".equals(locId))) {
            locId = "--";
        }
        return network + seperator + station + seperator + locId + seperator + channel + seperator
                + df.format(beginTime) + seperator + df.format(endTime);
    }

    String network;

    String station;

    String location;

    String channel;

    Instant beginTime;

    Instant endTime;
}
