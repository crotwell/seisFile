package edu.sc.seis.seisFile.earthworm;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import edu.sc.seis.seisFile.earthworm.EarthwormEscapeOutputStream;
import edu.sc.seis.seisFile.earthworm.EarthwormExport;
import edu.sc.seis.seisFile.earthworm.EarthwormImport;
import edu.sc.seis.seisFile.earthworm.EarthwormMessage;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.winston.WinstonUtil;


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
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
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
        assertTrue("wire bytes", TraceBuf2.HEADER_SIZE+data.length*4 <= wireBytes.length);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(wireBytes);
        EarthwormImport importer = new EarthwormImport(bis);
        EarthwormMessage message = importer.nextMessage();
        assertEquals("module", module, message.getModule());
        assertEquals("institution", institution, message.getInstitution());
        assertEquals("type", EarthwormMessage.MESSAGE_TYPE_TRACEBUF2, message.getMessageType());
        assertTrue("message bytes "+TraceBuf2.HEADER_SIZE+data.length*4 +" > "+ message.getData().length, TraceBuf2.HEADER_SIZE+data.length*4 <= message.getData().length);

        TraceBuf2 out = new TraceBuf2(message.getData());
        assertEquals("size", tb.getSize(), out.getSize());
        assertEquals("dataType", tb.getDataType(), out.getDataType());
        assertEquals("start", tb.getStartTime(), out.getStartTime(), 0.000001);
        assertEquals("end", tb.getEndTime(), out.getEndTime(), 0.000001);
        assertEquals("net", tb.getNetwork(), out.getNetwork());
        assertEquals("sta", tb.getStation(), out.getStation());
        assertEquals("loc", tb.getLocId(), out.getLocId());
        assertEquals("chan", tb.getChannel(), out.getChannel());
        assertEquals("version", tb.getVersion(), out.getVersion());
        assertEquals("quality", tb.getQuality(), out.getQuality());
        assertEquals("pad", tb.getPad(), out.getPad());
        assertArrayEquals(data, out.getIntData());
        assertArrayEquals("data", tb.getIntData(), out.getIntData());
    }
}
