package edu.sc.seis.seisFile.gcf;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;


public class SerialTransportHeaderTest {

    @Test
    public void testWrite() throws IOException, GCFFormatException {
        boolean isSerial = true;
        int seqNum = 1;
        GCFBlock block = GCFBlock.mockGCF(new Date(), new int[200], isSerial);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        SerialTransportLayer layer = new SerialTransportLayer(seqNum, block, isSerial);
        layer.getHeader().write(new DataOutputStream(bout));
        bout.close();
        byte[] boutBytes = bout.toByteArray();
        assertEquals("header byte size", 4, boutBytes.length);
        assertEquals("G byte", 'G', boutBytes[0]);
        assertEquals("seq", (byte)seqNum, boutBytes[1]);
        assertEquals("block size: "+boutBytes[2]+"("+((boutBytes[2] & 0xff) << 8)+") "+boutBytes[3], block.getSize(), ((boutBytes[2] & 0xff) << 8) + (boutBytes[3] & 0xff));
    }
}
