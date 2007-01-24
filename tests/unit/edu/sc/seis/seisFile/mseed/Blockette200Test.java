package edu.sc.seis.seisFile.mseed;

import junit.framework.TestCase;

public class Blockette200Test extends TestCase {

    public void testInternalConsistency() {
        Blockette200 fromParams = new Blockette200(2.0f,
                                             1.0f,
                                             3.0f,
                                             new Btime(),
                                             "I detected the event.");
        assertEquals(fromParams, new Blockette200(fromParams.info));
        assertEquals(fromParams.getSignal(), 2.0f, 0.0);
        assertEquals(fromParams.getPeriod(), 1.0f, 0.0);
        assertEquals(fromParams.getSignalOnset(), new Btime());
        assertEquals("I detected the event.", fromParams.getEventDetector().trim());
    }
}
