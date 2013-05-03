package edu.sc.seis.seisFile.gcf;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;


public class GCFHeaderTest {

    @Test
    public void mockStart() {
        boolean isSerial = true;
        int[] data = new int[200];
        for (int i = 0; i < data.length; i++) {
            data[i] = i % 20;
            if (i % 20 > 10 ) {data[i] *= -1;}
            data[i] *= 10000;
        }
        GCFBlock block = GCFBlock.mockGCF(Convert.convertTime(0, 0).getTime(), data, isSerial);
        assertEquals("day", 0, block.getHeader().getDayNumber());
        assertEquals("Sec", 0, block.getHeader().getSecondsInDay());
        assertEquals("end", 2, block.getHeader().getPredictedNextStartDaySec()[1]);
        
    }
    @Test
    public void testRoundTrip() throws Exception, IOException {
        boolean isSerial = true;
        int[] data = new int[200];
        for (int i = 0; i < data.length; i++) {
            data[i] = i % 20;
            if (i % 20 > 10 ) {data[i] *= -1;}
            data[i] *= 10000;
        }
        GCFBlock block = GCFBlock.mockGCF(new Date(), data, isSerial);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        block.getHeader().write(out);
        out.close();
        byte[] stlBytes = bout.toByteArray();
        /*
        for (int i = 0; i < stlBytes.length; i++) {
            System.out.print(stlBytes[i]+" ");
        }
        System.out.println();
        */
        assertEquals("saved bytes ", GCFHeader.SIZE, stlBytes.length);
        GCFHeader outHeader = GCFHeader.read(new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(stlBytes))));
        checkEquals(block.getHeader(), outHeader);
    }
    
    public static void checkEquals(GCFHeader expectedHeader, GCFHeader actualHeader) {
        assertEquals("systemid equals", expectedHeader.getSystemId(), actualHeader.getSystemId());
        assertEquals("streamid equals", expectedHeader.getStreamId(), actualHeader.getStreamId());
        assertEquals("day number equals", expectedHeader.getDayNumber(), actualHeader.getDayNumber());
        assertEquals("seconds equals", expectedHeader.getSecondsInDay(), actualHeader.getSecondsInDay());
        assertEquals("compression equals", expectedHeader.getCompression(), actualHeader.getCompression());
        assertEquals("sps equals", expectedHeader.getSps(), actualHeader.getSps());
        assertEquals("num32 equals", expectedHeader.getNum32Records(), actualHeader.getNum32Records());
    }
}
