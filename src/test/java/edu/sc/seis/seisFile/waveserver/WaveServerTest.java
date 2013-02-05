package edu.sc.seis.seisFile.waveserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.syncFile.SyncFileCompareTest;
import edu.sc.seis.seisFile.winston.WinstonUtil;


public class WaveServerTest extends TestCase {

    @Test
    public void testGetMenu() throws Exception {
        DataInputStream in = new DataInputStream(loadResource("MENU.out"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        WaveServer ws = new WaveServer(out, in);
        List<MenuItem> items = ws.getMenu();
        assertTrue("items is empty", 0 != items.size());
        assertEquals("items is 69", 69, items.size());
    }
    @Test
    public void testGetMenuSCNL() throws Exception {
        DataInputStream in = new DataInputStream(loadResource("MENUSCNL.out"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        WaveServer ws = new WaveServer(out, in);
        List<MenuItem> items = ws.getMenu();
        assertTrue("items is empty", 0 != items.size());
        assertEquals("items is 204", 204, items.size());
    }
    
    @Test
    public void testBigTraceBuf() throws Exception {

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
        // without compression
        List<DataRecord> mseedList = tb.toMiniSeed(12, false);
        int numPoints = 0;
        for (DataRecord dr : mseedList) {
            numPoints += dr.getHeader().getNumSamples();
        }
        assertEquals("num points without compression", tb.getNumSamples(), numPoints);
        // with compression
        mseedList = tb.toMiniSeed(12, true);
        numPoints = 0;
        for (DataRecord dr : mseedList) {
            numPoints += dr.getHeader().getNumSamples();
        }
        assertEquals("num points for compression", tb.getNumSamples(), numPoints);
        
    }


    static BufferedInputStream loadResource(String filename) throws IOException, SeisFileException {
        return new BufferedInputStream(SyncFileCompareTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/waveserver/" + filename));
    }
}
