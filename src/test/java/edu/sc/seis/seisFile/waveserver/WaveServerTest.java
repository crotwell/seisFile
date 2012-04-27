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
import edu.sc.seis.seisFile.syncFile.SyncFileCompareTest;


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
    


    static BufferedInputStream loadResource(String filename) throws IOException, SeisFileException {
        return new BufferedInputStream(SyncFileCompareTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/waveserver/" + filename));
    }
}
