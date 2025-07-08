package edu.sc.seis.seisFile.mseed;

import org.junit.jupiter.api.Test;


import java.time.Instant;


import edu.sc.seis.seisFile.TimeUtils;

import static org.junit.jupiter.api.Assertions.*;


public class BtimeTest {
    
    @Test
    public void testEquals(){
        assertEquals(new Btime(), new Btime());
        assertEquals(new Btime(), new Btime(new Btime().getAsBytes()));
    }

    @Test
    public void testGreaterThan() {
        Btime s1 = new Btime(2011, 59, 17, 11, 3, 1751);
        Btime s2 = new Btime(2011, 59, 17, 11, 13, 3500);
        Btime p1 = new Btime(2011, 59, 17, 0, 8, 1000);
        Btime p2 = new Btime(2011, 59, 17, 0, 18, 3250);
        assertTrue(s1.after(p1));
        assertTrue(s1.after(p2));
        assertTrue(s2.after(p1));
        assertTrue(s2.after(p2));
    }

    @Test
    public void testCalendarInAfternoon() {
        Btime s1 = new Btime(2011, 59, 17, 13, 3, 1750); // tenth milles are lost in calendar conversion
        Instant cal = s1.toInstant();
        Btime out = new Btime(cal);
        assertEquals(s1, out);
    }
    
    @Test
    public void testBtimeFromDouble() {
        Btime s1 = new Btime(2011, 59, 17, 11, 3, 1751);
        double d = TimeUtils.instantToEpochSeconds(s1.toInstant());
        Btime out = new Btime(d);
        assertEquals(s1, out);
    }

    /**
     * Test all years and jdays, should be swap=false.
     * But this is inconclusive for year=2056 and jdays=1,256,257...sigh
     */
    @Test
    public void testByteSwap() {
        for (int year = Btime.MIN_YEAR; year < Btime.MAX_YEAR; year++) {
            for (int jday = 1; jday < 366; jday++) {
                Btime bt = new Btime(year, jday, 0, 0, 0, 0);
                byte[] bytes = bt.getAsBytes();
                assertFalse(Btime.shouldSwapBytes(bytes), "year: "+year+" jday: "+ jday);
                if (year != 2056 || ! (jday == 1 || jday == 256 || jday == 257) ) {
                    // these days in 2056 are indeterminate...
                    byte[] swapped = new byte[bytes.length];
                    System.arraycopy(bytes, 0, swapped, 0, bytes.length);
                    swapped[0] = bytes[1];
                    swapped[1] = bytes[0];
                    swapped[2] = bytes[3];
                    swapped[3] = bytes[2];
                    assertTrue(Btime.shouldSwapBytes(swapped), "year: " + year + " jday: " + jday);
                }
            }
        }
    }
    
}
