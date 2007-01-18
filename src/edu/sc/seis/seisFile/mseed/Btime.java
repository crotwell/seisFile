/**
 * Btime.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

public class Btime {

    public Btime() {}

    public Btime(byte[] bytes) {
        this(bytes, 0);
    }

    public Btime(byte[] bytes, int offset) {
        boolean byteSwapFlag = shouldSwapBytes(bytes);
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

    public int year = 1900;

    public int jday = 1;

    public int hour = 0;

    public int min = 0;

    public int sec = 0;

    public int tenthMilli = 0;

    /**
     * Expects btime to be a byte array pointing at the beginning of a btime
     * segment
     * 
     * @return - true if the bytes need to be swapped to get a valid year
     */
    public static boolean shouldSwapBytes(byte[] btime) {
        int year = Utility.uBytesToInt(btime[0], btime[1], false);
        return year < 1960 || year > 2050;
    }

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
