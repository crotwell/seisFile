package edu.sc.seis.seisFile.gcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.junit.Test;

public class SerialTransportLayerTest {

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
        SerialTransportLayer stl = new SerialTransportLayer(new SerialTransportHeader(3, block.getSize()), 
                                                            block);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        stl.write(out);
        out.close();
        byte[] stlBytes = bout.toByteArray();
        assertEquals("GCFBlock size", GCFHeader.SIZE+(isSerial?3:4)*data.length+2*4, stl.getPayload().getSize());
        int expectedSize = SerialTransportHeader.SIZE+stl.getPayload().getSize()+2;
        assertEquals("saved bytes "+SerialTransportHeader.SIZE+" "+GCFHeader.SIZE+" "+(isSerial?3:4)*data.length+" "+2*4+" "+2, expectedSize, stlBytes.length);
        SerialTransportLayer outStl = SerialTransportLayer.read(new BufferedInputStream(new ByteArrayInputStream(stlBytes)));
        assertEquals(stl, outStl);
    }

}
