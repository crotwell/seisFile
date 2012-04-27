package edu.sc.seis.seisFile.waveserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
        this.location = location;
        if ("--".equals(location)) {
            this.location = "  ";
        }
        this.start = start;
        this.end = end;
        this.pin = pin;
        this.dataType = dataType;
    }

    public String toString() {
        return network + " " + station + " " + location + " " + channel + " " + formatDate(toDate(start)) + " "
                + formatDate(toDate(end));
    }

    public static Date toDate(double val) {
        return new Date(Math.round(1000 * val));
    }

    public static String formatDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
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

    public Date getStartDate() {
        return toDate(start);
    }

    public Date getEndDate() {
        return toDate(end);
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
