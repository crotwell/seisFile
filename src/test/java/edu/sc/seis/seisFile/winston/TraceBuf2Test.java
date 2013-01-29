package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.junit.Test;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class TraceBuf2Test {

    @Test
    public void testToBytesRoundTrip() throws DataFormatException, IOException {
        int numSamples = 6343;
        TraceBuf2 tb = createTraceBuf(numSamples);
        byte[] dataBytes = tb.toByteArray();
        TraceBuf2 out = new TraceBuf2(dataBytes);
        assertEquals("size", tb.getSize(), out.getSize());
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
        assertArrayEquals(tb.getIntData(), out.getIntData());
        assertArrayEquals("data", tb.getIntData(), out.getIntData());
    }

    @Test
    public void testSpaceSpaceLocId() throws DataFormatException, IOException {
        int numSamples = 6343;
        TraceBuf2 tb = createTraceBuf(numSamples);
        tb.locId = "  ";
        byte[] dataBytes = tb.toByteArray();
        TraceBuf2 out = new TraceBuf2(dataBytes);
        assertEquals("loc", "--", out.getLocId());
    }

    @Test
    public void testSplit() {
        for (int numSamples = 18101; numSamples < 18200; numSamples += 100) {
            TraceBuf2 tb = createTraceBuf(numSamples);
            List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
            checkSplit(numSamples, tb, splitList);
        }
    }

    void checkSplit(int numSamples, TraceBuf2 tb, List<TraceBuf2> splitList) {
        assertEquals("start for " + numSamples, tb.getStartTime(), splitList.get(0).getStartTime(), 0.000001);
        int totSamps = 0;
        int splitNum = 0;
        double nextStart = tb.getStartTime();
        for (TraceBuf2 splitTB : splitList) {
            assertEquals(numSamples + " " + splitNum + ": pin", tb.getPin(), splitTB.getPin());
            assertEquals(numSamples + " " + splitNum + ": dataType", tb.getDataType(), splitTB.getDataType());
            assertEquals(numSamples + " " + splitNum + ": net", tb.getNetwork(), splitTB.getNetwork());
            assertEquals(numSamples + " " + splitNum + ": sta", tb.getStation(), splitTB.getStation());
            assertEquals(numSamples + " " + splitNum + ": loc", tb.getLocId(), splitTB.getLocId());
            assertEquals(numSamples + " " + splitNum + ": chan", tb.getChannel(), splitTB.getChannel());
            assertEquals(numSamples + " " + splitNum + ": version", tb.getVersion(), splitTB.getVersion());
            assertEquals(numSamples + " " + splitNum + ": quality", tb.getQuality(), splitTB.getQuality());
            assertEquals(numSamples + " " + splitNum + ": pad", tb.getPad(), splitTB.getPad());
            totSamps += splitTB.getNumSamples();
            assertEquals(numSamples + " " + splitNum + ": start", nextStart, splitTB.getStartTime(), 0.0000001);
            assertEquals(numSamples + " " + splitNum + ": end",
                         nextStart + (splitTB.getNumSamples() - 1) / splitTB.getSampleRate(),
                         splitTB.getEndTime(),
                         0.000001);
            nextStart = tb.getStartTime() + totSamps / splitTB.getSampleRate();
            assertTrue(numSamples + " " + splitNum + ": less than max size: " + splitTB.getSize() + " > "
                    + TraceBuf2.MAX_TRACEBUF_SIZE, TraceBuf2.MAX_TRACEBUF_SIZE >= splitTB.getSize());
            assertEquals(numSamples + " " + splitNum + ": dataType", tb.getDataType(), splitTB.getDataType());
            splitNum++;
        }
        assertEquals(numSamples + ": total num points", tb.getNumSamples(), totSamps);
        assertEquals(numSamples + ": end", tb.getEndTime(), splitList.get(splitList.size() - 1).getEndTime(), 0.000001);
    }

    @Test
    public void testSteveSplitBeforeMicroOverlap() throws ParseException {
        int numSamples = 4016;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = sdf.parse("2012-07-23 18:21:53.840");
        TraceBuf2 tb = createTraceBuf(numSamples, start);
        System.out.println("Tracebuf: "+tb);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        checkSplit(numSamples, tb, splitList);
    }

    @Test
    public void testSteveSplit() throws ParseException {
        int numSamples = 4875;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = sdf.parse("2012-07-23 18:22:33.990");
        TraceBuf2 tb = createTraceBuf(numSamples, start);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        checkSplit(numSamples, tb, splitList);
    }

    @Test
    public void testToMiniSeed() throws SeedFormatException {
        TraceBuf2 tb = createTraceBuf(4000);
        List<DataRecord> mseedList = tb.toMiniSeed(9, true);
        assertEquals("start", tb.getStartDate(), mseedList.get(0)
                .getHeader()
                .getStartBtime()
                .convertToCalendar()
                .getTime());
        assertEquals("end", tb.getEndDate(), mseedList.get(mseedList.size() - 1)
                .getHeader()
                .getLastSampleBtime()
                .convertToCalendar()
                .getTime());
        Date nextSampleTime = tb.getStartDate();
        int numPts = 0;
        for (DataRecord dr : mseedList) {
            Blockette1000 b1000 = (Blockette1000)dr.getUniqueBlockette(1000);
            assertEquals("dataType", B1000Types.STEIM1, b1000.getEncodingFormat());
            assertEquals("net", tb.getNetwork(), dr.getHeader().getNetworkCode());
            assertEquals("sta", tb.getStation(), dr.getHeader().getStationIdentifier());
            assertEquals("loc", tb.getLocId(), dr.getHeader().getLocationIdentifier());
            assertEquals("chan", tb.getChannel(), dr.getHeader().getChannelIdentifier());
            Date drStart = dr.getHeader().getStartBtime().convertToCalendar().getTime();
            assertEquals("previous dr date", nextSampleTime, drStart);
            assertTrue("start before end",
                       drStart.before(dr.getHeader().getLastSampleBtime().convertToCalendar().getTime()));
            numPts += dr.getHeader().getNumSamples();
            nextSampleTime = dr.getHeader().getPredictedNextStartBtime().convertToCalendar().getTime();
        }
        assertTrue("nextSample after end", nextSampleTime.after(tb.getEndDate()));
        assertEquals("npts", tb.getNumSamples(), numPts);
    }

    TraceBuf2 createTraceBuf(int numSamples) {
        return createTraceBuf(numSamples, new Date());
    }

    TraceBuf2 createTraceBuf(int numSamples, Date start) {
        int[] data = new int[numSamples];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i % 256) - 128;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     start.getTime()/1000.0,
                                     100,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     data);
        assertArrayEquals(data, tb.getIntData());
        return tb;
    }
}
