package edu.sc.seis.seisFile.syncFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SyncLine implements Comparable<SyncLine> {

    public static SyncLine parse(String line) throws ParseException, NumberFormatException {
        String[] s = line.split("\\|", -1);
        SyncLine out = new SyncLine(s[0], // net
                                    s[1], // sta
                                    s[2], // loc
                                    s[3], // chan
                                    stringToDate(s[4]), // startTime
                                    stringToDate(s[5]), // endTime
                                    stringToFloat(s[6]), // maxClockDrift
                                    stringToFloat(s[7]), // samplesPerSecond
                                    s[8], // channelFlag
                                    s[9], // stationVolume
                                    s[10], // dccTapeNumber
                                    s[11], // dmcTapeNumber
                                    s[12], // comment
                                    stringToDate(s[13]), // lineModByDMC
                                    stringToDate(s[14])); // lineModByDCC
        return out;
    }

    public SyncLine(SyncLine copy, Date startTime, Date endTime, Float samplesPerSecond) {
        this(copy, startTime, endTime);
        this.samplesPerSecond = samplesPerSecond;
    }

    public SyncLine(SyncLine copy, Date startTime, Date endTime) {
        this(copy.net,
             copy.sta,
             copy.loc,
             copy.chan,
             startTime,
             endTime,
             copy.maxClockDrift,
             copy.samplesPerSecond,
             copy.channelFlag,
             copy.stationVolume,
             copy.dccTapeNumber,
             copy.dmcTabpNumber,
             copy.comment,
             copy.lineModByDMC,
             copy.lineModByDCC);
    }

    public SyncLine(String net,
                    String sta,
                    String loc,
                    String chan,
                    Date startTime,
                    Date endTime,
                    Float maxClockDrift,
                    Float samplesPerSecond) {
        this(net, sta, loc, chan, startTime, endTime, maxClockDrift, samplesPerSecond, "", "", "", "", "", null, null);
    }

    public SyncLine(String net, String sta, String loc, String chan) {
        this(net, sta, loc, chan, null, null, null, null);
    }

    public SyncLine(String net,
                    String sta,
                    String loc,
                    String chan,
                    Date startTime,
                    Date endTime,
                    Float maxClockDrift,
                    Float samplesPerSecond,
                    String channelFlag,
                    String stationVolume,
                    String dccTapeNumber,
                    String dmcTabpNumber,
                    String comment,
                    Date lineModByDMC,
                    Date lineModByDCC) {
        super();
        this.net = net;
        this.sta = sta;
        this.loc = loc;
        this.chan = chan;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxClockDrift = maxClockDrift;
        this.samplesPerSecond = samplesPerSecond;
        this.channelFlag = channelFlag;
        this.stationVolume = stationVolume;
        this.dccTapeNumber = dccTapeNumber;
        this.dmcTabpNumber = dmcTabpNumber;
        this.comment = comment;
        this.lineModByDMC = lineModByDMC;
        this.lineModByDCC = lineModByDCC;
    }

    public String formatLine() {
        return concatWithSeparator(new String[] {net,
                                                 sta,
                                                 loc,
                                                 chan,
                                                 dateToString(startTime),
                                                 dateToString(endTime),
                                                 maxClockDrift == null ? "" : "" + maxClockDrift,
                                                 samplesPerSecond == null ? "" : "" + samplesPerSecond,
                                                 channelFlag,
                                                 stationVolume,
                                                 dccTapeNumber,
                                                 dmcTabpNumber,
                                                 comment,
                                                 dateToString(lineModByDMC),
                                                 dateToString(lineModByDCC)}, SyncFile.SEPARATOR);
    }

    /**
     * returns true if line is from the same channel and immediately after this
     * SyncLine.
     */
    public boolean isContiguous(SyncLine line, float tolerenceSeconds) {
        return net.equals(line.net) && sta.equals(line.sta) && loc.equals(line.loc) && chan.equals(line.chan)
                && (line.startTime.getTime() == endTime.getTime() || line.startTime.after(endTime))
                && ((line.startTime.getTime() - endTime.getTime()) / 1000.0 + 1 / samplesPerSecond) <= tolerenceSeconds;
    }

    public SyncLine concat(SyncLine after) {
        return new SyncLine(this, this.startTime, after.endTime);
    }

    public int compareTo(SyncLine two) {
        int subComp = getNet().compareToIgnoreCase(two.getNet());
        if (subComp != 0) {
            return subComp;
        }
        subComp = getSta().compareToIgnoreCase(two.getSta());
        if (subComp != 0) {
            return subComp;
        }
        subComp = getLoc().compareToIgnoreCase(two.getLoc());
        if (subComp != 0) {
            return subComp;
        }
        subComp = getChan().compareToIgnoreCase(two.getChan());
        if (subComp != 0) {
            return subComp;
        }
        return getStartTime().compareTo(two.getStartTime());
    }

    public static String concatWithSeparator(String[] items, String separator) {
        String out = items[0];
        for (int i = 1; i < items.length; i++) {
            out += separator;
            if (items[i] != null) {
                out += items[i].trim();
            }
        }
        return out;
    }

    /**
     * parse the date from a string, checking for null and empty. If empty, a
     * null Date is returned.
     * 
     * @throws NumberFormatException
     *             if string can not be parsed
     */
    public static Float stringToFloat(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        return Float.parseFloat(s);
    }

    /**
     * parse the date from a string, checking for null and empty. If empty, a
     * null Date is returned.
     * 
     * @throws ParseException
     *             if string is not of the form "yyyy,DDD,hh:mm:ss"
     */
    public static Date stringToDate(String d) throws ParseException {
        if (d == null || d.length() == 0) {
            return null;
        }
        if (d.length() == 8) {
            // year and day only
            synchronized(dayOnlyDateFormat) {
                return dayOnlyDateFormat.parse(d);
            }
        } else {
            synchronized(dateFormat) {
                return dateFormat.parse(d);
            }
        }
    }

    /**
     * format the date as a string, checking for null. If null, an empty String
     * is returned.
     */
    public static String dateToString(Date d) {
        if (d == null) {
            return "";
        }
        synchronized(dateFormat) {
            return dateFormat.format(d);
        }
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy,DDD,HH:mm:ss.SSS");

    private static final DateFormat dayOnlyDateFormat = new SimpleDateFormat("yyyy,DDD");
    
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        dayOnlyDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public String getNet() {
        return net;
    }

    public String getSta() {
        return sta;
    }

    public String getLoc() {
        return loc;
    }

    public String getChan() {
        return chan;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Float getMaxClockDrift() {
        return maxClockDrift;
    }

    public Float getSamplesPerSecond() {
        return samplesPerSecond;
    }

    public String getChannelFlag() {
        return channelFlag;
    }

    public String getStationVolume() {
        return stationVolume;
    }

    public String getDccTapeNumber() {
        return dccTapeNumber;
    }

    public String getDmcTabpNumber() {
        return dmcTabpNumber;
    }

    public String getComment() {
        return comment;
    }

    public Date getLineModByDMC() {
        return lineModByDMC;
    }

    public Date getLineModByDCC() {
        return lineModByDCC;
    }

    String net;

    String sta;

    String loc;

    String chan;

    Date startTime;

    Date endTime;

    Float maxClockDrift;

    Float samplesPerSecond;

    String channelFlag;

    String stationVolume;

    String dccTapeNumber;

    String dmcTabpNumber;

    String comment;

    Date lineModByDMC;

    Date lineModByDCC;
}
