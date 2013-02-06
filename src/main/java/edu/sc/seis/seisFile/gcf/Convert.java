package edu.sc.seis.seisFile.gcf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        if (scnl == null) {
            scnl = new String[4];
            scnl[0] = h.getStreamId().substring(0, 4);
            scnl[1] = "EN"+h.getStreamId().charAt(4);
            scnl[2] = "XX";
            scnl[3] = "0"+h.getStreamId().charAt(5);
        }
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

    public static Calendar convertTime(int dayNumber, int secInDay) {
        Calendar cal = Calendar.getInstance(TZ_GMT);
        cal.set(Calendar.YEAR, 1989);
        cal.set(Calendar.DAY_OF_YEAR, NOV_17_DAY_OF_YEAR);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, dayNumber);
        cal.add(Calendar.SECOND, secInDay);
        return cal;
    }
    
    public static int[] convertTime(Date dateTime) {
        int[] daySec = new int[2];
        Calendar cal = Calendar.getInstance(TZ_GMT);
        cal.setTime(dateTime);
        daySec[0] = 45; // Jan 1 1990 is 45
        daySec[0] += 365 * (cal.get(Calendar.YEAR)-1990);
        if (cal.get(Calendar.YEAR) % 4 == 0 && cal.get(Calendar.DAY_OF_YEAR) >= 31+29) {
            // leap year, so Nov 17 is one more
            daySec[0]-=1;
        }
        daySec[0] += cal.get(Calendar.DAY_OF_YEAR)-1;  // days are 1 based
        daySec[0] += (cal.get(Calendar.YEAR)-1988)/4; // 1992  -> 1, 1996 -> 2, 2000-> 3, 2004 -> 4
        daySec[1] = cal.get(Calendar.SECOND);
        daySec[1] += cal.get(Calendar.MINUTE)*60;
        daySec[1] += cal.get(Calendar.HOUR_OF_DAY)*3600;
        return daySec;
    }
    
 // Nov 17, 1989 is day 0 (thanks Guralp for making that an easy one to remember)
    public static final int NOV_17_DAY_OF_YEAR = 321;
    
    Map<String, String[]> sysId_streamIdToSCNL;
    
    public static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT");
}
