package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;


public class TraceBuf2Test {
    
    @Test
    public void testSplit() {
        int numSamples = 6343;
        int[] data = new int[numSamples];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i % 256) - 128;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS + numSamples,
                                     1,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     "a",
                                     TraceBuf2.SUN_IEEE_INTEGER,
                                     "a",
                                     "",
                                     data);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        
        assertEquals("start",  tb.getStartTime(), splitList.get(0).getStartTime(), 0.000001);
        int totSamps = 0;
        for (TraceBuf2 splitTB : splitList) {
            totSamps += splitTB.getNumSamples();
            assertTrue("less than max size: "+splitTB.getSize()+" > "+TraceBuf2.MAX_TRACEBUF_SIZE, TraceBuf2.MAX_TRACEBUF_SIZE >= splitTB.getSize());
            assertEquals("dataType", tb.getDataType(), splitTB.getDataType());
        }
        assertEquals("total num points", tb.getNumSamples(), totSamps);
        assertEquals("end",  tb.getEndTime(), splitList.get(splitList.size()-1).getEndTime(), 0.000001);
    }

}
