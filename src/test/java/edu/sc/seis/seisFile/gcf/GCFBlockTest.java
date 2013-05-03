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
        data[0] = Short.MAX_VALUE+1;
        GCFBlock mock = GCFBlock.mockGCF(new Date(), data, isSerial);
        assertEquals("serial size", GCFHeader.SIZE+(isSerial?3:4)*data.length + 8, mock.getSize());
        isSerial = false;
        mock = GCFBlock.mockGCF(new Date(), data, isSerial);
        assertEquals("non-serial size", GCFHeader.SIZE+(isSerial?3:4)*data.length + 8, mock.getSize());
    }
    

    @Test
    public void testRoundTrip() throws Exception {
        int[] data = new int[200];
        data[0] = 1;
        for (int i = 0; i < data.length; i++) {
            data[i] = i % 20;
            if (i % 20 > 10 ) {data[i] *= -1;}
        }
        testRoundTrip(data);
        for (int i = 0; i < data.length; i++) {
            data[i] *= 128;
        }
        testRoundTrip(data);
        for (int i = 0; i < data.length; i++) {
            data[i] *= 128;
        }
    }

    public void testRoundTrip(int[] data) throws Exception {
        boolean isSerial = true;
        GCFBlock block = GCFBlock.mockGCF(new Date(), data, isSerial);
        assertArrayEquals(data, block.getUndiffData());
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        block.write(out);
        out.close();
        byte[] stlBytes = bout.toByteArray();
        int dataSize = data.length;
        if (block.getHeader().getCompression() == 1 && block.isSerial) {
            dataSize = data.length * 3;
        } else {
            dataSize = data.length * 4 / block.getHeader().getCompression();
        }
        assertEquals("GCFBlock size", GCFHeader.SIZE+dataSize+2*4, block.getSize());
        int expectedSize = GCFHeader.SIZE+dataSize+2*4;
        assertEquals("saved bytes c="+block.getHeader().getCompression()+" "+GCFHeader.SIZE+" "+dataSize+" "+2*4+" ", expectedSize, stlBytes.length);
        GCFBlock outBlock = (GCFBlock)AbstractGCFBlock.read(new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(stlBytes))),
                                                            isSerial);
        GCFHeaderTest.checkEquals(block.getHeader(), outBlock.getHeader());
        assertEquals("size", block.getSize(), outBlock.getSize());
        assertEquals("first", block.getFirstSample(), outBlock.getFirstSample());
        assertEquals("last", block.getLastSample(), outBlock.getLastSample());
        assertArrayEquals(block.getUndiffData(), outBlock.getUndiffData());
        assertArrayEquals(block.getDiffData(), outBlock.getDiffData());
        
        assertTrue(block.equals(outBlock));
    }
}
