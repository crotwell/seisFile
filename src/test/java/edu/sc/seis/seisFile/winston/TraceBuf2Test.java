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
        for (int numSamples = 1000; numSamples < 8200; numSamples++) {
        int[] data = new int[numSamples];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i % 256) - 128;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
                                     .01,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     data);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        
        assertEquals("start for "+numSamples,  tb.getStartTime(), splitList.get(0).getStartTime(), 0.000001);
        int totSamps = 0;
        int splitNum = 0;
        double nextStart = tb.getStartTime();
        for (TraceBuf2 splitTB : splitList) {

            assertEquals(numSamples+" "+splitNum+": pin", tb.getPin(), splitTB.getPin());
            assertEquals(numSamples+" "+splitNum+": dataType", tb.getDataType(), splitTB.getDataType());
            assertEquals(numSamples+" "+splitNum+": net", tb.getNetwork(), splitTB.getNetwork());
            assertEquals(numSamples+" "+splitNum+": sta", tb.getStation(), splitTB.getStation());
            assertEquals(numSamples+" "+splitNum+": loc", tb.getLocId(), splitTB.getLocId());
            assertEquals(numSamples+" "+splitNum+": chan", tb.getChannel(), splitTB.getChannel());
            assertEquals(numSamples+" "+splitNum+": version", tb.getVersion(), splitTB.getVersion());
            assertEquals(numSamples+" "+splitNum+": quality", tb.getQuality(), splitTB.getQuality());
            assertEquals(numSamples+" "+splitNum+": pad", tb.getPad(), splitTB.getPad());
            
            totSamps += splitTB.getNumSamples();
            assertEquals(numSamples+" "+splitNum+": start", nextStart, splitTB.getStartTime(), 0.0000001);
            assertEquals(numSamples+" "+splitNum+": end", nextStart+(splitTB.getNumSamples()-1)/splitTB.getSampleRate(), splitTB.getEndTime(), 0.0000001);
            nextStart = splitTB.getEndTime()+1/splitTB.getSampleRate();
            assertTrue(numSamples+" "+splitNum+": less than max size: "+splitTB.getSize()+" > "+TraceBuf2.MAX_TRACEBUF_SIZE, TraceBuf2.MAX_TRACEBUF_SIZE >= splitTB.getSize());
            assertEquals(numSamples+" "+splitNum+": dataType", tb.getDataType(), splitTB.getDataType());
            splitNum++;
        }
        assertEquals(numSamples+": total num points", tb.getNumSamples(), totSamps);
        assertEquals(numSamples+": end",  tb.getEndTime(), splitList.get(splitList.size()-1).getEndTime(), 0.000001);
    }
    }

}
