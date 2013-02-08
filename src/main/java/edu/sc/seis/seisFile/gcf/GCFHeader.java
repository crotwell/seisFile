package edu.sc.seis.seisFile.gcf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import edu.iris.dmc.seedcodec.Utility;


public class GCFHeader {

    public GCFHeader(String systemId,
                     String streamId,
                     int dayNumber,
                     int secondsInDay,
                     int sps,
                     int compression,
                     int num32Records) {
        super();
        if ( ! (compression == 1 || compression == 2 || compression == 4)) {
            throw new IllegalArgumentException("Compression of "+compression+" not allowed, must be 1,2,4");
        }
        if (num32Records > 1000) {
            throw new IllegalArgumentException("max num 32 bit record is 1000, but was "+num32Records);
        }
        this.systemId = systemId.toUpperCase();
        this.streamId = streamId.toUpperCase();
        this.dayNumber = dayNumber;
        this.secondsInDay = secondsInDay;
        this.sps = sps;
        this.compression = compression;
        this.num32Records = num32Records;
    }
    
    public void write(DataOutput out) throws NumberFormatException, IOException {
        out.writeInt(Integer.parseInt(getSystemId(), 36));
        out.writeInt(Integer.parseInt(getStreamId(), 36));
        int dayPlusBit = (getDayNumber() << 1) + ((getSecondsInDay() >> 16) & 0x1);
        out.writeShort(dayPlusBit);
        out.writeShort(getSecondsInDay() & 0xffff);
        out.writeByte(0); // unused byte
        out.write(getSps());
        out.write(getCompression());
        out.write(getNum32Records());
    }
    
    public static GCFHeader fromBytes(byte[] data) {
        return new GCFHeader(Integer.toString(Utility.bytesToInt(data[0], data[1], data[2], data[3], false), 36),
                             Integer.toString(Utility.bytesToInt(data[4], data[5], data[6], data[7], false), 36),
                             Utility.bytesToInt(data[8], data[9], false) >> 1,
                             Utility.bytesToInt((byte)(data[9] & 0x1), data[10], data[11], false),
                             data[13] & 0xff, data[14] & 0xff, data[15] & 0xff);
        
    }
    
    public static GCFHeader read(DataInput in) throws IOException {
        byte[] data = new byte[SIZE];
        in.readFully(data);
        return fromBytes(data);
    }
    
    
    public String getSystemId() {
        return systemId;
    }

    
    public String getStreamId() {
        return streamId;
    }

    
    public int getDayNumber() {
        return dayNumber;
    }

    
    public int getSecondsInDay() {
        return secondsInDay;
    }
    
    public Date getStartAsDate() {
        return Convert.convertTime(getDayNumber(), getSecondsInDay()).getTime();
    }
    
    public Date getLastSampleTime() {
        return new Date(getStartAsDate().getTime()+Math.round((getNumPoints()-1.0)/getSps()*1000));
    }


    public Date getPredictedNextStartTime() {
        return new Date(getStartAsDate().getTime()+Math.round(((double)getNumPoints())/getSps()*1000));
    }
    
    public int[] getPredictedNextStartDaySec() {
        int day = getDayNumber();
        int sec = getSecondsInDay()+(int)Math.round(((double)getNumPoints())/getSps());
        if (sec >= 86400) {
            // what about leap seconds???
            sec -= 86400;
            day++;
        }
        return new int[] { day, sec};
    }
    
    public int getSps() {
        return sps;
    }

    
    public int getCompression() {
        return compression;
    }

    
    public int getNum32Records() {
        return num32Records;
    }
    
    public int getNumPoints() {
        return getNum32Records()*getCompression();
    }
    
    public String toString() {
        return "GCFHeader: "+systemId+" "+streamId+" "+dayNumber+" "+secondsInDay+" "+sps+" "+compression+" "+num32Records;
    }

    String systemId;
    String streamId;
    int dayNumber;
    int secondsInDay;
    int sps;
    int compression;
    int num32Records;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + compression;
        result = prime * result + dayNumber;
        result = prime * result + num32Records;
        result = prime * result + secondsInDay;
        result = prime * result + sps;
        result = prime * result + ((streamId == null) ? 0 : streamId.hashCode());
        result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
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
        GCFHeader other = (GCFHeader)obj;
        if (compression != other.compression)
            return false;
        if (dayNumber != other.dayNumber)
            return false;
        if (num32Records != other.num32Records)
            return false;
        if (secondsInDay != other.secondsInDay)
            return false;
        if (sps != other.sps)
            return false;
        if (streamId == null) {
            if (other.streamId != null)
                return false;
        } else if (!streamId.equals(other.streamId))
            return false;
        if (systemId == null) {
            if (other.systemId != null)
                return false;
        } else if (!systemId.equals(other.systemId))
            return false;
        return true;
    }

    public static final int SIZE = 16;
}
