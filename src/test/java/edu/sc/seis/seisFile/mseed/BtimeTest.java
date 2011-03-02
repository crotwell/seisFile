package edu.sc.seis.seisFile.mseed;

import junit.framework.TestCase;


public class BtimeTest extends TestCase {
    public void testEquals(){
        assertEquals(new Btime(), new Btime());
        assertEquals(new Btime(), new Btime(new Btime().getAsBytes()));
    }
    
    public void testGreaterThan() {
        Btime s1 = new Btime(2011, 59, 17, 11, 3, 1750);
        Btime s2 = new Btime(2011, 59, 17, 11, 13, 3500);
        Btime p1 = new Btime(2011, 59, 17, 0, 8, 1000);
        Btime p2 = new Btime(2011, 59, 17, 0, 18, 3250);
        assertTrue(s1.greaterThan(p1));
        assertTrue(s1.greaterThan(p2));
        assertTrue(s2.greaterThan(p1));
        assertTrue(s2.greaterThan(p2));
    }
}
