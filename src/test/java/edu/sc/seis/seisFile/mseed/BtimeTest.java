package edu.sc.seis.seisFile.mseed;

import junit.framework.TestCase;


public class BtimeTest extends TestCase {
    public void testEquals(){
        assertEquals(new Btime(), new Btime());
        assertEquals(new Btime(), new Btime(new Btime().getAsBytes()));
    }
    
}
