package edu.sc.seis.seisFile.mseed;

import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Test;
import junit.framework.TestCase;
import static org.junit.Assert.*;

import edu.sc.seis.seisFile.sac.TestSacFileData;

public class DataRecordTest {

    @Test
    public void read512RecordSize() throws IOException, SeedFormatException {
        DataRecord dr = DataRecord.read(new DataInputStream(TestSacFileData.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/mseed/test_no1001_512_steim2")));
        assertEquals(dr.getRecordSize(), 512);
    }
}
