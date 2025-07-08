/**
 * Btime.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import edu.sc.seis.seisFile.TimeUtils;

public class Btime {
    
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public Btime(Instant date) {
        ZonedDateTime zdt = date.atZone(TimeUtils.TZ_UTC);
        setFieldsFromZonedDateTime(zdt);
    }

    private void setFieldsFromZonedDateTime(ZonedDateTime zdt) {
        if (zdt.getOffset().getTotalSeconds() != 0) {
            throw new IllegalArgumentException("Calendar time zone is not UTC: "+zdt.getZone());
        }
        tenthMilli = zdt.getNano()/TimeUtils.NANOS_IN_TENTH_MILLI;
        year = zdt.getYear();
        jday = zdt.getDayOfYear();
        hour = zdt.getHour();
        min = zdt.getMinute();
        sec = zdt.getSecond();
    }
    
    public Btime() {}
    
    public Btime(int year, int jday, int hour, int min, int sec, int tenthMilli) {
        this.year = year;
        this.jday = jday;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.tenthMilli = tenthMilli;
    }

    public Btime(byte[] bytes) {
        this(bytes, 0);
    }

    public Btime(byte[] bytes, int offset) {
        this(bytes, offset, shouldSwapBytes(bytes, offset));
    }

    public Btime(byte[] bytes, int offset, boolean byteSwapFlag) {
        year = Utility.uBytesToInt(bytes[offset],
                                   bytes[offset + 1],
                                   byteSwapFlag);
        jday = Utility.uBytesToInt(bytes[offset + 2],
                                   bytes[offset + 3],
                                   byteSwapFlag);
        hour = bytes[offset + 4] & 0xff;
        min = bytes[offset + 5] & 0xff;
        sec = bytes[offset + 6] & 0xff;
        // bytes[7] is unused (alignment)
        tenthMilli = Utility.uBytesToInt(bytes[offset + 8],
                                         bytes[offset + 9],
                                         byteSwapFlag);
    }

    /** Create with seconds since epoch (1970) */
    public Btime(double d) {
        this(TimeUtils.instantFromEpochSeconds(d));
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + hour;
        result = PRIME * result + jday;
        result = PRIME * result + min;
        result = PRIME * result + sec;
        result = PRIME * result + tenthMilli;
        result = PRIME * result + year;
        return result;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o instanceof Btime) {
            Btime oBtime = (Btime)o;
            return oBtime.year == year && oBtime.jday == jday
                    && oBtime.hour == hour && oBtime.min == min
                    && oBtime.sec == sec && oBtime.tenthMilli == tenthMilli;
        }
        return false;
    }
    
    public boolean before(Btime other) {
        return comparator.compare(this, other) == -1;
    }
    
    public boolean after(Btime other) {
        return comparator.compare(this, other) == 1;
    }
    
    public boolean afterOrEquals(Btime other) {
        return comparator.compare(this, other) >= 0;
    }

    public Instant toInstant() {
        // leap seconds????
        if (getSec() == 60) {
            throw new RuntimeException("Leap seconds not yet implemented???");
        }
        return ZonedDateTime.of(getYear(),
                                Month.JANUARY.getValue(),
                                1,
                                getHour(),
                                getMin(),
                                getSec(),
                                getTenthMilli()*TimeUtils.NANOS_IN_TENTH_MILLI,
                                TimeUtils.TZ_UTC)
                .plus(Duration.ofDays(getDayOfYear()-1))
                .toInstant();
    }

    public String toString() {
        return "BTime(" + year + ":" + jday + ":" + hour + ":" + min + ":"
                + sec + "." + tenthMilli + ")";
    }

    public int year = 1960;

    public int jday = 1;

    public int hour = 0;

    public int min = 0;

    public int sec = 0;

    public int tenthMilli = 0;

    
    public int getYear() {
        return year;
    }

    public int getDayOfYear() {
        return getJDay();
    }
    
    public int getJDay() {
        return jday;
    }

    
    public int getHour() {
        return hour;
    }

    
    public int getMin() {
        return min;
    }

    
    public int getSec() {
        return sec;
    }

    
    public int getTenthMilli() {
        return tenthMilli;
    }

    /**
     * Expects btime to be a byte array pointing at the beginning of a btime
     * segment
     * 
     * @return - true if the bytes need to be swapped to get a valid year
     */
    public static boolean shouldSwapBytes(byte[] btime) {
        return shouldSwapBytes(btime, 0);
    }

    public static int MIN_YEAR = 1802;
    public static int MAX_YEAR = 2310;
    public static int MIN_JDAY = 1;
    public static int MAX_JDAY = 366;

    /*
    Python code to print byte-swap pairs between 1800 and 2500:
        for i in range(1800, 2500):
          bs = (i-i%256)/256 + (i % 256)*256
          if 1800 <= bs <= 2500:
            print(f"{i}  {bs}")
     */
    /**
     * Expects btime to be a byte array pointing at the beginning of a btime
     * segment.
     * 
     * Time capsule: note that year 2056 as a short byte swaps to itself, so whomever
     * is maintaining this code off in the distant future, 49 years from now as
     * I write this in 2007, should find some other header to use for byte swap checking!
     *
     * Also, 1800 swaps to 2055, so careful on the low end as well.
     * Other swaps that overlap (in 1800 to 2500 range) are:
     * 1800  2055
     * 1801  2311
     * 2055  1800
     * 2056  2056
     * 2057  2312
     * 2311  1801
     * 2312  2057
     * 2313  2313
     *
     * So, 1802 to 2310 are a safe "good" year range, with the exception of 2056
     *
     * Using the jday helps but isn't definitive as 1 byte swaps to 256,
     * 256 to 1 and 257 to itself.
     * Similar tenthmilli has same as jday as well as 3 swaps to 768
     * 
     * If mseed was going to support little endian headers they should have put in a damn flag!
     *  - HPC
     * 
     * @return - true if the bytes need to be swapped to get a valid year/jday, false if
     *           not needed or if it is not determinable (year 2056).
     */
    public static boolean shouldSwapBytes(byte[] btime, int offset) {
        int year = Utility.uBytesToInt(btime[0 + offset],
                                       btime[1 + offset],
                                       false);
        int jday = Utility.uBytesToInt(btime[offset + 2],
                btime[offset + 3],
                false);
        boolean validYearDay = year >= MIN_YEAR && year <= MAX_YEAR
                            && jday >= MIN_JDAY && jday <= MAX_JDAY;
        if ( ! validYearDay) {
            // year, jday invalid, try swap and see if it helps
            year = Utility.uBytesToInt(btime[0 + offset],
                    btime[1 + offset],
                    true);
            jday = Utility.uBytesToInt(btime[offset + 2],
                    btime[offset + 3],
                    true);
            // if swapped looks valid, return true, otherwise leave as false???
            if (year >= MIN_YEAR && year <= MAX_YEAR && jday >= MIN_JDAY && jday <= MAX_JDAY) {
                return true;
            }
        }
        return false;
    }
    
    private static BtimeComparator comparator = new BtimeComparator();

    public byte[] getAsBytes() {
        byte[] bytes = new byte[10];
        System.arraycopy(Utility.intToByteArray(year), 2, bytes, 0, 2);
        System.arraycopy(Utility.intToByteArray(jday), 2, bytes, 2, 2);
        System.arraycopy(Utility.intToByteArray(hour), 3, bytes, 4, 1);
        System.arraycopy(Utility.intToByteArray(min), 3, bytes, 5, 1);
        System.arraycopy(Utility.intToByteArray(sec), 3, bytes, 6, 1);
        System.arraycopy(Utility.intToByteArray(tenthMilli), 2, bytes, 8, 2);
        return bytes;
    }
}
