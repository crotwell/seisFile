package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.junit.Test;

import edu.sc.seis.seisFile.earthworm.TraceBuf2;


public class WinstonUtilTest {

    @Test
    public void testSimpleJ2K() {
        Date d = WinstonUtil.j2KSecondsToDate(-1*WinstonUtil.Y1970_TO_Y2000_SECONDS);
        assertEquals(0, d.getTime());
        
        Date d1970 = new Date(0);
        assertEquals(-1* WinstonUtil.Y1970_TO_Y2000_SECONDS, WinstonUtil.dateToJ2kSeconds(d1970), 0.001);
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy,DDD,HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals("1970,001,00:00:00", dateFormat.format(d1970));
        assertEquals("1970,001,00:00:00", dateFormat.format(d));
    }

    @Test
    public void testRoundTrip() {
        long[] testVals = new long[] {0, -1, 1, 2, -123, 9000000, 65499023356l, };
        for (long l : testVals) {
            assertEquals("test round trip: ", l, WinstonUtil.dateToJ2kSeconds(WinstonUtil.j2KSecondsToDate(l)), 0.001);
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
        assertEquals("size", tb.getSize(), out.getSize());
        assertEquals("pin", tb.getPin(), out.getPin());
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
        assertArrayEquals("data", tb.getIntData(), out.getIntData());
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
        assertTrue("oops, buffer is not big enough", compressedDataLength <= compressedBytes.length); 
        byte[] tmp = new byte[compressedDataLength];
        System.arraycopy(compressedBytes, 0, tmp, 0, tmp.length);
        compressedBytes = tmp; tmp = null;
        
        TraceBuf2 out = winstonUtil.extractFromBlob(compressedBytes);

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
        assertArrayEquals(data, out.getIntData());
        
    }
}
