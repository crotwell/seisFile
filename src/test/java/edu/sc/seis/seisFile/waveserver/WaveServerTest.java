package edu.sc.seis.seisFile.waveserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.mseed.DataRecord;


public class WaveServerTest {

    @Test
    public void testGetMenu() throws Exception {
        DataInputStream in = new DataInputStream(loadResource("MENU.out"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        WaveServer ws = new WaveServer(out, in);
        List<MenuItem> items = ws.getMenu();
        assertEquals( 69, items.size());
    }
    @Test
    public void testGetMenuSCNL() throws Exception {
        DataInputStream in = new DataInputStream(loadResource("MENUSCNL.out"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        WaveServer ws = new WaveServer(out, in);
        List<MenuItem> items = ws.getMenu();
        assertEquals( 204, items.size());
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
                                     TraceBuf2.Y1970_TO_Y2000_SECONDS,
                                     1,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     data);
        // without compression
        List<DataRecord> mseedList = tb.toMiniSeedNoCompression(12);
        int numPoints = 0;
        for (DataRecord dr : mseedList) {
            numPoints += dr.getHeader().getNumSamples();
        }
        assertEquals(tb.getNumSamples(), numPoints);
        // with compression
        mseedList = tb.toMiniSeed(12, B1000Types.STEIM1);
        numPoints = 0;
        for (DataRecord dr : mseedList) {
            numPoints += dr.getHeader().getNumSamples();
        }
        assertEquals( tb.getNumSamples(), numPoints);
        
    }


    static BufferedInputStream loadResource(String filename) throws IOException, SeisFileException {
        return new BufferedInputStream(WaveServerTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/waveserver/" + filename));
    }
}
