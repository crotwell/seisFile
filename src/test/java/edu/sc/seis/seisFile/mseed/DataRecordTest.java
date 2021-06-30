package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.DataInputStream;
import java.io.IOException;


import edu.sc.seis.seisFile.sac.TestSacFileData;

public class DataRecordTest {

    @Test
    public void read512RecordSize() throws IOException, SeedFormatException {
        SeedRecord dr = DataRecord.read(new DataInputStream(TestSacFileData.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/mseed/test_no1001_512_steim2")));
        assertEquals(dr.getRecordSize(), 512);
    }


    @Test
    public void noDataButDataOffsetNonzero() throws IOException, SeedFormatException {
        DataInputStream dis = new DataInputStream(TestSacFileData.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/mseed/nodata_dataoffset.mseed"));
        while(dis.available() > 0) {
        DataRecord dr = (DataRecord)DataRecord.read(dis);
            assertEquals(dr.getRecordSize(), 512);
            assertEquals(dr.getHeader().getNumSamples(), 0);
            assertEquals(dr.getHeader().getDataOffset(), 256);
        }
    }
}
