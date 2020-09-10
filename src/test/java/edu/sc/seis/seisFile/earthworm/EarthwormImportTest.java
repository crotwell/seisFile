package edu.sc.seis.seisFile.earthworm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;


public class EarthwormImportTest {

    @Test
    public void testNextMessage() throws Exception {
        int numSamples = 500;
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
        
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EarthwormEscapeOutputStream ewOut = new EarthwormEscapeOutputStream(bos);
        int module = 19;
        int institution = 27;
        EarthwormExport ewExport = new EarthwormExport(ewOut, module, institution);
        ewExport.export(tb);
        byte[] wireBytes = bos.toByteArray();
        assertTrue( TraceBuf2.HEADER_SIZE+data.length*4 <= wireBytes.length);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(wireBytes);
        EarthwormImport importer = new EarthwormImport(bis);
        EarthwormMessage message = importer.nextMessage();
        assertEquals( module, message.getModule());
        assertEquals( institution, message.getInstitution());
        assertEquals( EarthwormMessage.MESSAGE_TYPE_TRACEBUF2, message.getMessageType());
        assertTrue( TraceBuf2.HEADER_SIZE+data.length*4 <= message.getData().length);

        TraceBuf2 out = new TraceBuf2(message.getData());
        assertEquals( tb.getSize(), out.getSize());
        assertEquals( tb.getDataType(), out.getDataType());
        assertEquals( tb.getStartTime(), out.getStartTime(), 0.000001);
        assertEquals( tb.getEndTime(), out.getEndTime(), 0.000001);
        assertEquals( tb.getNetwork(), out.getNetwork());
        assertEquals( tb.getStation(), out.getStation());
        assertEquals( tb.getLocId(), out.getLocId());
        assertEquals( tb.getChannel(), out.getChannel());
        assertEquals( tb.getVersion(), out.getVersion());
        assertEquals( tb.getQuality(), out.getQuality());
        assertEquals( tb.getPad(), out.getPad());
        assertArrayEquals(data, out.getIntData());
        assertArrayEquals( tb.getIntData(), out.getIntData());
    }
}
