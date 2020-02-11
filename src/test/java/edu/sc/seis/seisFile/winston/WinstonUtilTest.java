package edu.sc.seis.seisFile.winston;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;


import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;


public class WinstonUtilTest {

    @Test
    public void testSimpleJ2K() {
        Instant d = WinstonUtil.j2KSecondsToDate(-1*WinstonUtil.Y1970_TO_Y2000_SECONDS);
        assertEquals(0, d.getEpochSecond());
        
        Instant d1970 = Instant.ofEpochSecond(0);
        assertEquals(-1* WinstonUtil.Y1970_TO_Y2000_SECONDS, WinstonUtil.dateToJ2kSeconds(d1970), 0.001);
        
        DateTimeFormatter dateFormat = TimeUtils.createFormatter("yyyy,DDD,HH:mm:ss").withZone(TimeUtils.TZ_UTC);
        assertEquals("1970,001,00:00:00", dateFormat.format(d1970));
        assertEquals("1970,001,00:00:00", dateFormat.format(d));
    }

    @Test
    public void testRoundTrip() {
        long[] testVals = new long[] {0, -1, 1, 2, -123, 9000000, 65499023356l, };
        for (long l : testVals) {
            assertEquals(l, WinstonUtil.dateToJ2kSeconds(WinstonUtil.j2KSecondsToDate(l)), 0.001, "test round trip: ");
        }
    }
    
    @Test
    public void testTraceBuf2FromBytes() throws DataFormatException, IOException {
        WinstonUtil winstonUtil = new WinstonUtil("","","","","");
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
        TraceBuf2 out = new TraceBuf2(tb.toByteArray());
        assertEquals( tb.getSize(), out.getSize());
        assertEquals( tb.getPin(), out.getPin());
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
        assertArrayEquals( tb.getIntData(), out.getIntData());
    }
    
    @Test
    public void testTraceBuf2FromBlob() throws DataFormatException, IOException {
        WinstonUtil winstonUtil = new WinstonUtil("","","","","");
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
        byte[] compressedBytes = new byte[dataBytes.length];
        Deflater compresser = new Deflater();
        compresser.setInput(dataBytes);
        compresser.finish();
        int compressedDataLength = compresser.deflate(compressedBytes);
        assertTrue(compressedDataLength <= compressedBytes.length, "oops, buffer is not big enough"); 
        byte[] tmp = new byte[compressedDataLength];
        System.arraycopy(compressedBytes, 0, tmp, 0, tmp.length);
        compressedBytes = tmp; tmp = null;
        
        TraceBuf2 out = winstonUtil.extractFromBlob(compressedBytes);

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
        assertArrayEquals(data, out.getIntData());
        
    }
}
