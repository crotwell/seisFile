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
        assertEquals("systemid equals", block.getHeader().getSystemId(), outHeader.getSystemId());
        assertEquals("streamid equals", block.getHeader().getStreamId(), outHeader.getStreamId());
        assertEquals("day number equals", block.getHeader().getDayNumber(), outHeader.getDayNumber());
        assertEquals("seconds equals", block.getHeader().getSecondsInDay(), outHeader.getSecondsInDay());
        assertEquals("compression equals", block.getHeader().getCompression(), outHeader.getCompression());
        assertEquals("sps equals", block.getHeader().getSps(), outHeader.getSps());
        assertEquals("num32 equals", block.getHeader().getNum32Records(), outHeader.getNum32Records());
        assertEquals("header equals", block.getHeader(), outHeader);
    }
}
