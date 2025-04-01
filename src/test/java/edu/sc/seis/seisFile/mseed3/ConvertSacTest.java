package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed3.ehbag.Marker;
import edu.sc.seis.seisFile.sac.SacConstants;
import edu.sc.seis.seisFile.sac.SacTimeSeries;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertSacTest {


    @Test
    public void testConvertSacToMS3() throws IOException, SeedFormatException, FDSNSourceIdException {
        String sacFile = "19981216.BB12.mod_with_taupsetsac.SAC";
        DataInputStream dis = new DataInputStream(new BufferedInputStream(ReferenceDataTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/sac/"+sacFile)));
        SacTimeSeries sac = SacTimeSeries.read(dis);
        assertEquals("BHZ", sac.getHeader().getKcmpnm().trim());
        assertEquals(1998, sac.getHeader().getNzyear());
        assertEquals(350, sac.getHeader().getNzjday());
        assertEquals(15, sac.getHeader().getNzhour());
        assertEquals(59, sac.getHeader().getNzmin());
        assertEquals(44, sac.getHeader().getNzsec());
        assertEquals(625, sac.getHeader().getNzmsec());
        ZonedDateTime nz = ZonedDateTime.parse("1998-12-16T15:59:44.625Z[UTC]");
        assertEquals(nz, sac.getHeader().getNzTime());
        sac.getHeader().setA(100);
        assertEquals(16.875f, sac.getHeader().getO());

        MSeed3Record ms3 = MSeed3Convert.convertSacTo3(sac);
        assertEquals(sac.getHeader().getKcmpnm().trim(), ms3.getSourceId().getChannelCode());
        MSeed3EH ms3eh = new MSeed3EH(ms3.getExtraHeaders());
        System.err.println(ms3eh.getBagEH().toString(2));
        List<Marker> markList = ms3eh.getMarkers();
        assertEquals(5, markList.size());
        for (Marker mark : markList) {
            if (mark.getName().equals("A")) {
                Duration d = Duration.between(sac.getHeader().getNzTime(), mark.getTime());

                assertEquals(d.getSeconds()+1e-9*d.getNano(), sac.getHeader().getA());
            }
        }
    }
}
