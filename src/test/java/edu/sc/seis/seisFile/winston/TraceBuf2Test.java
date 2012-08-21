package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.junit.Test;


public class TraceBuf2Test {

    @Test
    public void testToBytesRoundTrip() throws DataFormatException, IOException {
        int numSamples = 6343;
        int[] data = new int[numSamples];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i % 256) - 128;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
                                     1,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     data);

        assertArrayEquals(data, tb.getIntData());
        
        byte[] dataBytes = tb.toByteArray();
        
        TraceBuf2 out = new TraceBuf2(dataBytes);

        assertEquals("dataType", tb.getDataType(), out.getDataType());
        assertEquals("start", tb.getStartTime(), out.getStartTime(), 0.000001);
        assertEquals("end", tb.getEndTime(), out.getEndTime(), 0.000001);
        assertEquals("net", tb.getNetwork(), out.getNetwork());
        assertEquals("sta", tb.getStation(), out.getStation());
        assertEquals("loc", tb.getLocId(), out.getLocId());
        assertEquals("chan", tb.getChannel(), out.getChannel());
        assertEquals("version", tb.getVersion(), out.getVersion());
        assertEquals("quality", tb.getQuality(), out.getQuality());
        assertEquals("pad", tb.getPad(), out.getPad());
        assertArrayEquals(data, out.getIntData());
        assertArrayEquals("data", tb.getIntData(), out.getIntData());
    }
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
                                     1,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
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
