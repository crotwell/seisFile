package edu.sc.seis.seisFile.syncFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SyncLine {
    

    public SyncLine(SyncLine copy,
                    Date startTime,
                    Date endTime,
                    float samplesPerSecond) {
        this(copy, startTime, endTime);
        this.samplesPerSecond = samplesPerSecond;
    }
    
    public SyncLine(SyncLine copy,
                    Date startTime,
                    Date endTime) {
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
                    float maxClockDrift,
                    float samplesPerSecond) {
        this(net, sta, loc, chan, startTime, endTime, maxClockDrift, samplesPerSecond, "", "", "", "", "", null, null);
    }
    
    public SyncLine(String net,
                    String sta,
                    String loc,
                    String chan,
                    Date startTime,
                    Date endTime,
                    float maxClockDrift,
                    float samplesPerSecond,
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
                                                 "" + maxClockDrift,
                                                 "" + samplesPerSecond,
                                                 channelFlag,
                                                 stationVolume,
                                                 dccTapeNumber,
                                                 dmcTabpNumber,
                                                 comment,
                                                 dateToString(lineModByDMC),
                                                 dateToString(lineModByDCC)}, SyncFile.SEPARATOR);
    }

    /** returns true if line is from the same channel and immediately after this SyncLine. */
    public boolean isContiguous(SyncLine line, float tolerenceSeconds) {
        return  net.equals(line.net) &&
                sta.equals(line.sta) &&
                loc.equals(line.loc) &&
                chan.equals(line.chan) && 
                line.startTime.after(endTime) && 
                ((line.startTime.getTime()-endTime.getTime())/1000.0 + 1/samplesPerSecond) < tolerenceSeconds;
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
     * format the date as a string, checking for null. If null, an empty String
     * is returned.
     */
    public static String dateToString(Date d) {
        if (d == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyy,DDD,hh:mm:ss");
        return df.format(d);
    }
    
    String net;
    String sta;
    String loc;
    String chan;
    Date startTime;
    Date endTime;
    float maxClockDrift;
    float samplesPerSecond;
    String channelFlag;
    String stationVolume;
    String dccTapeNumber;
    String dmcTabpNumber;
    String comment;
    Date lineModByDMC;
    Date lineModByDCC;
}
