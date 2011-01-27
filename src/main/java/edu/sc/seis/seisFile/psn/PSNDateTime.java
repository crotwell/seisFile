package edu.sc.seis.seisFile.psn;

/**
 * PSNDateTime.java
 *
 * @author Created by Philip Oliver-Paull
 */

import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.sac.SacTimeSeries;

public class PSNDateTime {
    private DataInputStream dis;

    private short year;
    private byte month, day, hour, minute, second;
    private int nanosec;

    public PSNDateTime(DataInputStream data) throws IOException{
        dis = data;

        year = SacTimeSeries.swapBytes((short)dis.readUnsignedShort());
        month = dis.readByte();
        day = dis.readByte();
        hour = dis.readByte();
        minute = dis.readByte();
        second = dis.readByte();
        dis.readByte();
        nanosec = SacTimeSeries.swapBytes(dis.readInt());
    }

    //a constructor for testing purposes
    public PSNDateTime(short year, byte month, byte day, byte hour, byte minute, byte second, int nanosec){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nanosec = nanosec;
    }

    public short getYear() {
        return year;
    }

    public byte getMonth() {
        return month;
    }

    public byte getDay() {
        return day;
    }

    public byte getHour() {
        return hour;
    }

    public byte getMinute() {
        return minute;
    }

    public byte getSecond() {
        return second;
    }

    public int getNanosec() {
        return nanosec;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append(month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second + ":" + nanosec);

        return buf.toString();
    }

}

