/**
 * Btime.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.seisFile.mseed;

public class Btime {
    public int year = 1900;
    public int jday = 1;
    public int hour = 0;
    public int min = 0;
    public int sec = 0;
    public int tenthMilli = 0;

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

