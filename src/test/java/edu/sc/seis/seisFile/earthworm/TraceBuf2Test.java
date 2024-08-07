package edu.sc.seis.seisFile.earthworm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.iris.dmc.seedcodec.SteimFrameBlock;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.zip.DataFormatException;


import edu.iris.dmc.seedcodec.B1000Types;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.TimeUtilsTest;
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
        assertEquals( tb.getSize(), out.getSize());
        assertEquals( tb.getDataType(), out.getDataType());
        assertEquals( tb.getStartTime(), out.getStartTime(), 0.000001);
        assertEquals( tb.getEndTime(), out.getEndTime(), 0.000001);
        assertEquals( tb.getNetwork(), out.getNetwork());
        assertEquals( tb.getStation(), out.getStation());
        assertEquals( tb.getLocId(), out.getLocId());
        assertEquals( tb.getChannel(), out.getChannel());
        assertEquals( tb.getVersion(), out.getVersion());
        assertEquals( tb.getQuality(), out.getQuality());
        assertEquals( tb.getPad(), out.getPad());
        assertArrayEquals(tb.getIntData(), out.getIntData());
        assertArrayEquals( tb.getIntData(), out.getIntData());
    }

    @Test
    public void testSpaceSpaceLocId() throws DataFormatException, IOException {
        int numSamples = 6343;
        TraceBuf2 tb = createTraceBuf(numSamples);
        tb.locId = "  ";
        byte[] dataBytes = tb.toByteArray();
        TraceBuf2 out = new TraceBuf2(dataBytes);
        assertEquals("--", out.getLocId(), "loc");
    }

    @Test
    public void testSplit() {
        for (int numSamples = 18101; numSamples < 18200; numSamples += 100) {
            TraceBuf2 tb = createTraceBuf(numSamples);
            List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
            checkSplit(numSamples, tb, splitList);
        }
    }

    public static void checkSplit(int numSamples, TraceBuf2 tb, List<TraceBuf2> splitList) {
        assertEquals(tb.getStartTime(), splitList.get(0).getStartTime(), 0.000001, "start for " + numSamples);
        int totSamps = 0;
        int splitNum = 0;
        double nextStart = tb.getStartTime();
        for (TraceBuf2 splitTB : splitList) {
            assertEquals(tb.getPin(), splitTB.getPin(), numSamples + " " + splitNum + ": pin");
            assertEquals(tb.getDataType(), splitTB.getDataType(), numSamples + " " + splitNum + ": dataType");
            assertEquals(tb.getNetwork(), splitTB.getNetwork(), numSamples + " " + splitNum + ": net");
            assertEquals(tb.getStation(), splitTB.getStation(), numSamples + " " + splitNum + ": sta");
            assertEquals(tb.getLocId(), splitTB.getLocId(), numSamples + " " + splitNum + ": loc");
            assertEquals(tb.getChannel(), splitTB.getChannel(), numSamples + " " + splitNum + ": chan");
            assertEquals(tb.getVersion(), splitTB.getVersion(), numSamples + " " + splitNum + ": version");
            assertEquals(tb.getQuality(), splitTB.getQuality(), numSamples + " " + splitNum + ": quality" );
            assertEquals(tb.getPad(), splitTB.getPad(), numSamples + " " + splitNum + ": pad" );
            totSamps += splitTB.getNumSamples();
            assertEquals(nextStart, splitTB.getStartTime(), 0.0000001, numSamples + " " + splitNum + ": start" );
            assertEquals(nextStart + (splitTB.getNumSamples() - 1) / splitTB.getSampleRate(),
                         splitTB.getEndTime(),
                         0.000001, numSamples + " " + splitNum + ": end");
            nextStart = tb.getStartTime() + totSamps / splitTB.getSampleRate();
            assertTrue(TraceBuf2.MAX_TRACEBUF_SIZE >= splitTB.getSize(), numSamples + " " + splitNum + ": less than max size: " + splitTB.getSize() + " > "
                    + TraceBuf2.MAX_TRACEBUF_SIZE);
            assertEquals(tb.getDataType(), splitTB.getDataType(), numSamples + " " + splitNum + ": dataType");
            splitNum++;
        }
        assertEquals(tb.getNumSamples(), totSamps, numSamples + ": total num points" );
        assertEquals(tb.getEndTime(), splitList.get(splitList.size() - 1).getEndTime(), 0.000001, numSamples + ": end");
    }

    @Test
    public void testSteveSplitBeforeMicroOverlap() throws ParseException {
        int numSamples = 4016;
        Instant start = TimeUtils.parseISOString("2012-07-23T18:21:53.840Z");
        TraceBuf2 tb = createTraceBuf(numSamples, start);
        System.out.println("Tracebuf: "+tb);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        checkSplit(numSamples, tb, splitList);
    }

    @Test
    public void testSteveSplit() throws ParseException {
        int numSamples = 4875;
        Instant start = TimeUtils.parseISOString("2012-07-23T18:22:33.990Z");
        TraceBuf2 tb = createTraceBuf(numSamples, start);
        List<TraceBuf2> splitList = tb.split(TraceBuf2.MAX_TRACEBUF_SIZE);
        checkSplit(numSamples, tb, splitList);
    }

    @Test
    public void testEncodeSteim1Zeros() throws SeedFormatException {
        int maxFrams = 7;
        int recLenExp = 9; // 512 => 7 frames
        int numSamples = 60*maxFrams-8; // first,last take up 8 bytes, all other one byte/sample except 4 byte nibbles
        int[] data = new int[numSamples+1]; // one more than will fit, all zeros
        Instant start = Instant.now();
        TraceBuf2 tb = new TraceBuf2(1,
                data.length,
                TimeUtils.instantToEpochSeconds(start),
                100,
                "JSC",
                "CO",
                "HHZ",
                "00",
                data);
        SteimFrameBlock sfb = tb.encodeSteim1(recLenExp);
        assertEquals(maxFrams, sfb.getNumFrames());
        assertEquals(numSamples, sfb.getNumSamples());
    }

    @Test
    public void testToMiniSeed() throws SeedFormatException {
        Duration TWO_TENTH_MILLI = TimeUtils.TENTH_MILLI.multipliedBy(2);
        TraceBuf2 tb = createTraceBuf(4000);
        List<DataRecord> mseedList = tb.toMiniSeed(9, B1000Types.STEIM1);
        TimeUtilsTest.assertInstantEquals(tb.getStartDate(),
                     mseedList.get(0).getHeader().getStartBtime().toInstant(), TWO_TENTH_MILLI, "start ");
        TimeUtilsTest.assertInstantEquals(tb.getEndDate(), mseedList.get(mseedList.size() - 1)
                .getLastSampleBtime().toInstant(), TWO_TENTH_MILLI, "end");
        Instant nextSampleTime = tb.getStartDate();
        int numPts = 0;
        for (DataRecord dr : mseedList) {
            Blockette1000 b1000 = (Blockette1000)dr.getUniqueBlockette(1000);
            assertEquals(B1000Types.STEIM1, b1000.getEncodingFormat(), "dataType");
            assertEquals(tb.getNetwork(), dr.getHeader().getNetworkCode());
            assertEquals(tb.getStation(), dr.getHeader().getStationIdentifier());
            assertEquals( tb.getLocId(), dr.getHeader().getLocationIdentifier());
            assertEquals( tb.getChannel(), dr.getHeader().getChannelIdentifier());
            Instant drStart = dr.getHeader().getStartBtime().toInstant();
            TimeUtilsTest.assertInstantEquals(nextSampleTime, drStart, TWO_TENTH_MILLI, "previous dr date");
            assertTrue(drStart.isBefore(dr.getLastSampleBtime().toInstant()));
            numPts += dr.getHeader().getNumSamples();
            nextSampleTime = dr.getPredictedNextStartBtime().toInstant();
        }
        assertTrue(nextSampleTime.isAfter(tb.getEndDate()));
        assertEquals(tb.getNumSamples(), numPts);
    }

    TraceBuf2 createTraceBuf(int numSamples) {
        return createTraceBuf(numSamples, Instant.now());
    }

    TraceBuf2 createTraceBuf(int numSamples, Instant start) {
        int[] data = new int[numSamples];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i % 256) - 128;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     TimeUtils.instantToEpochSeconds(start),
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
