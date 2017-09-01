package edu.sc.seis.seisFile.mseed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

import edu.sc.seis.seisFile.TimeUtils;



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
    
}
