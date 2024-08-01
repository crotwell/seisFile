package edu.sc.seis.seisFile.mseed3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import org.junit.jupiter.api.Test;

public class FDSNSourceIdTest {


    @Test
    public void parseTest() throws FDSNSourceIdException {
        String n = "XX";
        String s = "STA";
        String l = "00";
        String band = "B";
        String source = "H";
        String subsource = "Z";
        FDSNSourceId sid = FDSNSourceId.fromNSLC(n, s, l, band+source+subsource);
        assertEquals(n, sid.getNetworkCode());
        assertEquals(s, sid.getStationCode());
        assertEquals(l, sid.getLocationCode());
        assertEquals(band, sid.getBandCode());
        assertEquals(source, sid.getSourceCode());
        assertEquals(subsource, sid.getSubsourceCode());
        sid = FDSNSourceId.fromNSLC(n, s, l, band+"_"+source+"_"+subsource);
        assertEquals(band, sid.getBandCode());
        assertEquals(source, sid.getSourceCode());
        assertEquals(subsource, sid.getSubsourceCode());
        sid = FDSNSourceId.parse("FDSN:"+n+"_"+s+"_"+l+"_"+band+"_"+source+"_"+subsource);
        assertEquals(n, sid.getNetworkCode());
        assertEquals(s, sid.getStationCode());
        assertEquals(l, sid.getLocationCode());
        assertEquals(band, sid.getBandCode());
        assertEquals(source, sid.getSourceCode());
        assertEquals(subsource, sid.getSubsourceCode());

    }

}
