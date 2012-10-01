package edu.sc.seis.seisFile.mseed;

import static org.junit.Assert.*;

import org.junit.Test;



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
    public void testBtimeFromDouble() {
        Btime s1 = new Btime(2011, 59, 17, 11, 3, 1751);
        long millis = s1.convertToCalendar().getTimeInMillis();
        double d = millis/1000.0; // double seconds
        d += (s1.getTenthMilli() % 10) / 10000.0;
        Btime out = new Btime(d);
        assertEquals(s1, out);
    }
    
}
