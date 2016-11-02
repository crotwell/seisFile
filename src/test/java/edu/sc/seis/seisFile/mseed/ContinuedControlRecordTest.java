package edu.sc.seis.seisFile.mseed;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;



public class ContinuedControlRecordTest {

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void testReadDataInput() throws SeedFormatException, IOException {
        DataInputStream in = new DataInputStream(ContinuedControlRecordTest.class.getClassLoader()
                                                 .getResourceAsStream("edu/sc/seis/seisFile/mseed/LGCD_signal001958.part.txt"));
        SeedRecord dr = DataRecord.read(in, 4096);
        assertNotNull(dr);
    }
}
