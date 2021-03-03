package edu.sc.seis.seisFile.waveserver;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;

public class MenuItem {

    public MenuItem(String network,
                    String station,
                    String location,
                    String channel,
                    double start,
                    double end,
                    int pin,
                    String dataType) {
        super();
        this.station = station;
        this.network = network;
        this.channel = channel;
        this.location = Channel.fixLocCode(location);
        this.start = start;
        this.end = end;
        this.pin = pin;
        this.dataType = dataType;
    }

    public String toString() {
        return network + " " + station + " " + location + " " + channel + " " + formatDate(TimeUtils.instantFromEpochSeconds(start)) + " "
                + formatDate(TimeUtils.instantFromEpochSeconds(end));
    }

    public static String formatDate(Instant d) {
        DateTimeFormatter sdf = TimeUtils.createFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf.format(d);
    }

    public String getStation() {
        return station;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    public String getLocation() {
        return location;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public Instant getStartDate() {
        return TimeUtils.instantFromEpochSeconds(start);
    }

    public Instant getEndDate() {
        return TimeUtils.instantFromEpochSeconds(end);
    }

    public int getPin() {
        return pin;
    }

    public String getDataType() {
        return dataType;
    }

    String station;

    String network;

    String channel;

    String location;

    double start;

    double end;

    int pin;

    String dataType;
}
