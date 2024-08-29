package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.sac.SacTimeSeries;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ConvertSacTest {


    @Test
    public void testConvertSacToMS3() throws IOException, SeedFormatException, FDSNSourceIdException {
        String sacFile = "19981216.BB12.mod_with_taupsetsac.SAC";
        DataInputStream dis = new DataInputStream(new BufferedInputStream(ReferenceDataTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/sac/"+sacFile)));
        SacTimeSeries sac = SacTimeSeries.read(dis);
        assertEquals("BHZ", sac.getHeader().getKcmpnm().trim());

        MSeed3Record ms3 = MSeed3Convert.convertSacTo3(sac);
        assertEquals(sac.getHeader().getKcmpnm().trim(), ms3.getSourceId().getChannelCode());
    }
}
