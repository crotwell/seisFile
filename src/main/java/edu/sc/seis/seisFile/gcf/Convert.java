package edu.sc.seis.seisFile.gcf;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import edu.sc.seis.seisFile.earthworm.TraceBuf2;


public class Convert {

    public Convert(Map<String, String[]> sysId_streamIdToSCNL) {
        this.sysId_streamIdToSCNL = sysId_streamIdToSCNL;
    }
    
    public TraceBuf2 toTraceBuf(GCFBlock block) {
        GCFHeader h = block.getHeader();
        String[] scnl = sysId_streamIdToSCNL.get(h.getSystemId()+"_"+h.getStreamId());
        Calendar startTime = convertTime(h.getDayNumber(), h.getSecondsInDay());
        TraceBuf2 out = new TraceBuf2(0,
                                      h.getNumPoints(),
                                      startTime.getTimeInMillis()/1000.0,
                                      h.getSps(),
                                      scnl[0],
                                      scnl[2],
                                      scnl[1],
                                      scnl[3],
                                      block.getUndiffData());
        
        return out;
    }

    private Calendar convertTime(int dayNumber, int secInDay) {
        Calendar cal = Calendar.getInstance(TZ_GMT);
        cal.set(Calendar.YEAR, 1989);
        cal.set(Calendar.DAY_OF_YEAR, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, dayNumber);
        cal.add(Calendar.SECOND, secInDay);
        return cal;
    }
    
    Map<String, String[]> sysId_streamIdToSCNL;
    
    public static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT");
}
