package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.DataInputStream;
import java.io.IOException;


public class ContinuedControlRecordTest {

    public void setUp() throws Exception {}

    @Test
    public void testReadDataInput() throws SeedFormatException, IOException {
        DataInputStream in = new DataInputStream(ContinuedControlRecordTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/mseed/LGCD_signal001958.part.txt"));
        try {
            SeedRecord dr = DataRecord.read(in, 4096);
            assertNotNull(dr);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
