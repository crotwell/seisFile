package edu.sc.seis.seisFile;

import java.text.DateFormat;
import java.util.Date;

import edu.sc.seis.seisFile.fdsnws.AbstractQueryParams;

public class ChannelTimeWindow {

    public ChannelTimeWindow(String network,
                             String station,
                             String location,
                             String channel,
                             Date beginTime,
                             Date endTime) {
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
                             Date beginTime,
                             int durationSeconds) {
        this(network, station, location, channel, beginTime, new Date(beginTime.getTime() + 1000*durationSeconds));
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

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String toString() {
        return toString(" ", AbstractQueryParams.createDateFormat());
    }
    
    public String toString(String seperator, DateFormat df) {
        return network + seperator + station + seperator + location + seperator + channel + seperator
                + df.format(beginTime) + seperator + df.format(endTime);
    }

    String network;

    String station;

    String location;

    String channel;

    Date beginTime;

    Date endTime;
}
