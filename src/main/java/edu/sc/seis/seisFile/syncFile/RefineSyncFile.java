package edu.sc.seis.seisFile.syncFile;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.BasicConfigurator;

import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;

public class RefineSyncFile {

    public static void main(String[] args) throws IOException, SeisFileException {
        BasicConfigurator.configure();
        RefineSyncFile rsf = new RefineSyncFile();
        rsf.refine(args[0]);
    }
    
    public void refine(String syncfilename) throws IOException, SeisFileException {
        String inFilename = syncfilename;
        SyncFile sf = SyncFile.load(new File(inFilename));
        System.out.println("SyncFile "+sf.size()+" lines");
        String fileBase = SyncFileCompare.trimDotSync(inFilename);
        SyncFileWriter outSF = new SyncFileWriter(sf.getDccName()+" refined", fileBase+"_refined.sync");
        for (SyncLine sl : sf) {
            outSF.appendLine(refineTimes(sl));
        }
        outSF.close();
    }

    ChannelTimeWindow windowDate(String n, String s, String l, String c, Date d) {
        // syncfile from DMC is truncated to second, so go back by 1 millisecond
        // to make sure we get sample and look over next whole second
        return new ChannelTimeWindow(n, s, l, c, new Date(d.getTime() - 1), 1); 
    }

    List<ChannelTimeWindow> windowSyncLine(SyncLine sl) {
        List<ChannelTimeWindow> windowList = new ArrayList<ChannelTimeWindow>();
        windowList.add(windowDate(sl.getNet(), sl.getSta(), sl.getLoc(), sl.getChan(), sl.getStartTime()));
        Date endMinusOneSample = new Date(sl.getEndTime().getTime()-2*Math.round(1/minSPSForChanCode(sl.getChan())));
        windowList.add(windowDate(sl.getNet(), sl.getSta(), sl.getLoc(), sl.getChan(), endMinusOneSample));
        return windowList;
    }

    public SyncLine refineTimes(SyncLine sl, int retries) throws SeisFileException {
        int numRetry = 0;
        SeisFileException last = null;
        while(retries == 0 || numRetry < retries) {
            try {
                return refineTimes(sl);
            } catch (SeisFileException e) {
                numRetry++;
                last = e;
                if (e.getCause() instanceof SocketTimeoutException) {
                    // try again
                } else {
                    throw e;
                }
            }
        }
        throw last;
    }
    
    public SyncLine refineTimes(SyncLine sl) throws SeisFileException {
        System.out.println("SyncLine: "+sl);
        FDSNDataSelectQueryParams queryParams = new FDSNDataSelectQueryParams();
        List<ChannelTimeWindow> windowList = windowSyncLine(sl);
        FDSNDataSelectQuerier querier = new FDSNDataSelectQuerier(queryParams, windowList);
        DataRecordIterator it = querier.getDataRecordIterator();
        Date s = sl.getStartTime();
        Date e = sl.getEndTime();
        List<DataRecord> drList = new ArrayList<DataRecord>();
        try {
            while (it.hasNext()) {
                drList.add(it.next());
            }
            if (drList.size() == 0) {
                return sl;
            }
            // hopefully list has 2 DataRecords, one for beginning and one for
            // end
            DataRecord prev = null;
            boolean gapFound = false;
            for (DataRecord dr : drList) {
                System.out.println(dr.getHeader().getStartTime()+"  "+dr.getHeader().getEndTime()+" "+dr.getHeader().getPredictedNextStartBtime());
            }
            
            Date drS = drList.get(0).getHeader().getStartBtime().convertToCalendar().getTime();
            Date drE = drList.get(drList.size()-1).getHeader().getPredictedNextStartBtime().convertToCalendar().getTime();
            if (Math.abs(s.getTime()-drS.getTime()) < 1000) {
                s = drS;
            }
            if (Math.abs(e.getTime()-drE.getTime()) < 1000) {
                e = drE;
            }
            SyncLine out = new SyncLine(sl, s, e);
            System.out.println("Out "+out);
            return out;
        } catch(IOException ee) {
            throw new SeisFileException(ee);
        }
    }
    
    public static float minSPSForChanCode(String chan) {
        char c = chan.charAt(0);
        switch(c) {
            case 'F':
            case 'G':
                return 1000;
            case 'D':
            case 'C':
                return 250;
            case 'E':
            case 'H':
                return 80;
            case 'S':
            case 'B':
                return 10;
            case 'M':
            case 'L':
                return 1;
            case 'V':
                return 0.1f;
            case 'U':
                return .01f;
            case 'R':
                return 0.0001f;
            case 'P':
                return 0.00001f;
            case 'T':
                return 0.000001f;
            case 'Q':
                return 0.000001f;
        }
        return 1;
    }
}
