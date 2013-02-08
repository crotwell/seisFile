package edu.sc.seis.seisFile.gcf;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.junit.Test;


public class GCFBlockTest {

    @Test
    public void testGetSize() {
        boolean isSerial = true;
        int[] data = new int[200];
        GCFBlock mock = GCFBlock.mockGCF(new Date(), data, isSerial);
        assertEquals("serial size", GCFHeader.SIZE+(isSerial?3:4)*data.length + 8, mock.getSize());
        isSerial = false;
        mock = GCFBlock.mockGCF(new Date(), data, isSerial);
        assertEquals("non-serial size", GCFHeader.SIZE+(isSerial?3:4)*data.length + 8, mock.getSize());
    }
    

    @Test
    public void testRoundTrip() throws Exception {
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
        block.write(out);
        out.close();
        byte[] stlBytes = bout.toByteArray();
        assertEquals("GCFBlock size", GCFHeader.SIZE+(isSerial?3:4)*data.length+2*4, block.getSize());
        int expectedSize = GCFHeader.SIZE+(isSerial?3:4)*data.length+2*4;
        assertEquals("saved bytes "+GCFHeader.SIZE+" "+(isSerial?3:4)*data.length+" "+2*4+" ", expectedSize, stlBytes.length);
        GCFBlock outBlock = (GCFBlock)AbstractGCFBlock.read(new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(stlBytes))), isSerial);
        assertEquals(block, outBlock);
    }
}
