package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.ZonedDateTime;

public class ByteBufRoundTripTest {

    @Test
    public void roundTripTest() throws IOException, SeedFormatException {
        MSeed3Record ms3 = createRecord();
        assertEquals((byte) 4, ms3.timeseriesEncodingFormat);
        ByteBuffer buf = ms3.asByteBuffer();
        for (int i = 0; i < 40; i++) {
            System.out.print(buf.get(i) + " ");
            if (i % 4 == 3) {
                System.out.println();
            }
        }
        assertEquals(ms3.getSize(), buf.array().length);
        buf.position(0);
        MSeed3Record read_ms3 = MSeed3Record.fromByteBuffer(buf);
        compareRecords(ms3, read_ms3);
    }

    @Test
    public void startTimeTest() {
        MSeed3Record ms3 = createRecord();
        Instant st = TimeUtils.parseISOString("2024-02-29T13:47:59.9876543Z");
        ms3.setStartDateTime(st);
        ZonedDateTime zdt = ms3.getStartDateTime();
        assertEquals(st, zdt.toInstant());
        ms3.setStartDateTime(zdt);
        assertEquals(st, ms3.getStartInstant());
    }

    public void compareRecords(MSeed3Record ms3, MSeed3Record read_ms3) {
        assertEquals(ms3.formatVersion, read_ms3.formatVersion);
        assertEquals(ms3.flags, read_ms3.flags);
        assertEquals(ms3.nanosecond, read_ms3.nanosecond);
        assertEquals(ms3.year, read_ms3.year);
        assertEquals(ms3.dayOfYear, read_ms3.dayOfYear);
        assertEquals(ms3.hour, read_ms3.hour);
        assertEquals(ms3.minute, read_ms3.minute);
        assertEquals(ms3.second, read_ms3.second);
        assertEquals(ms3.timeseriesEncodingFormat, read_ms3.timeseriesEncodingFormat);
        assertEquals(ms3.sampleRatePeriod, read_ms3.sampleRatePeriod);
        assertEquals(ms3.numSamples, read_ms3.numSamples);
        assertEquals(ms3.recordCRC, read_ms3.recordCRC);
        assertEquals(ms3.publicationVersion, read_ms3.publicationVersion);

        assertEquals(ms3.getSourceIdStr(), read_ms3.getSourceIdStr());
        assertEquals(ms3.extraHeadersByteLength, read_ms3.extraHeadersByteLength);
        assertEquals(ms3.dataByteLength, read_ms3.dataByteLength);
    }


    @Test
    public void roundTripStreamStreamTest() throws IOException, SeedFormatException, FDSNSourceIdException {
        MSeed3Record ms3 = createRecord();
        assertEquals(400, ms3.dataByteLength);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ms3.write(bo);
        byte[] bytes = bo.toByteArray();
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(bi);
        MSeed3Record read_ms3 = MSeed3Record.read(din);
        compareRecords(ms3, read_ms3);

    }
    @Test
    public void roundTripStreamBufTest() throws IOException, SeedFormatException, FDSNSourceIdException {
        MSeed3Record ms3 = createRecord();
        assertEquals(400, ms3.dataByteLength);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ms3.write(bo);
        byte[] bytes = bo.toByteArray();
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        MSeed3Record read_ms3 = MSeed3Record.fromByteBuffer(buf);
        compareRecords(ms3, read_ms3);
    }

    @Test
    public void roundTripBufStreamTest() throws IOException, SeedFormatException, FDSNSourceIdException {
        MSeed3Record ms3 = createRecord();
        assertEquals(400, ms3.dataByteLength);
        byte[] bytes = ms3.asByteBuffer().array();
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(bi);
        MSeed3Record read_ms3 = MSeed3Record.read(din);
        compareRecords(ms3, read_ms3);

    }

    public MSeed3Record createRecord() {
        float[] data = new float[100];
        MSeed3Record ms3 = new MSeed3Record();
        ms3.nanosecond=1;
        ms3.dayOfYear=55;
        ms3.year = 33;
        ms3.hour = 1;
        ms3.minute = 2;
        ms3.second = 3;
        ms3.setTimeseries(data);
        ms3.sourceIdStr = "FDSN:XX_ABC_00_H_H_Z";
        return ms3;
    }
}
