package edu.sc.seis.seisFile.mseed3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;

import edu.sc.seis.seisFile.sac.SacConstants;
import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacTimeSeries;
import org.json.JSONObject;

import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette100;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.MissingBlockette1000;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class MSeed3Convert {

    public MSeed3Convert() {
    }

    public static MSeed3Record convertSacTo3(SacTimeSeries sac) throws SeedFormatException {
        MSeed3Record ms3 = new MSeed3Record();
        SacHeader sacHeader = sac.getHeader();
        ms3.year = sacHeader.getNzyear();
        ms3.dayOfYear = sacHeader.getNzjday();
        ms3.hour = sacHeader.getNzhour();
        ms3.minute = sacHeader.getNzmin();
        ms3.second = sacHeader.getNzsec();
        ms3.nanosecond = sacHeader.getNzmsec()*1000000;
        Instant start = ms3.getStartInstant();
        ms3.setStartDateTime(start.plusMillis(Math.round(sacHeader.getB()*1000)));
        if (sacHeader.getIftype() == SacConstants.ITIME) {
            ms3.setTimeseries(sac.getY());
        } else {
            throw new SeedFormatException("Sac file is not ITIME: "+sacHeader.getIftype());
        }
        return ms3;
    }

    public static MSeed3Record convert2to3(DataRecord dr) throws SeedFormatException {
        MSeed3Record ms3Header = new MSeed3Record();
        
        DataHeader ms2H = dr.getHeader();
        ms3Header.flags = (byte)((ms2H.getActivityFlags() & 1) *2
           + (ms2H.getIOClockFlags() & 64 ) * 4
           + (ms2H.getDataQualityFlags() & 16) * 8);
        ms3Header.setPublicationVersion((byte)0);

        ms3Header.year = ms2H.getStartBtime().year;
        ms3Header.dayOfYear = ms2H.getStartBtime().jday;
        ms3Header.hour = ms2H.getStartBtime().hour;
        ms3Header.minute = ms2H.getStartBtime().min;
        ms3Header.second = ms2H.getStartBtime().sec;
        ms3Header.nanosecond = ms2H.getStartBtime().tenthMilli*100000;
      // maybe can do better from factor and multiplier?
        ms3Header.sampleRatePeriod = dr.getSampleRate() >= 1 ? dr.getSampleRate() : (-1.0 / dr.getSampleRate());
        
        Blockette1000 b1000 = (Blockette1000)dr.getUniqueBlockette(1000);
        if (b1000 == null) {
            throw new MissingBlockette1000(dr.getHeader());
        }
        ms3Header.timeseriesEncodingFormat = b1000.getEncodingFormat();
        ms3Header.publicationVersion = MSeed3Record.UNKNOWN_DATA_VERSION;
        ms3Header.dataByteLength = dr.getData().length;
        ms3Header.setSourceId(FDSNSourceId.fromNSLC(dr.getHeader().getNetworkCode(),
                dr.getHeader().getStationIdentifier(),
                dr.getHeader().getLocationIdentifier(),
                dr.getHeader().getChannelIdentifier()));

        ms3Header.numSamples = ms2H.getNumSamples();
        ms3Header.recordCRC = 0;
        JSONObject ms3Extras = new JSONObject();
        JSONObject fdsnExtras = new JSONObject();
        if (ms2H.getTypeCode() != 0 && ms2H.getTypeCode() != 'D') {
            fdsnExtras.put("DataQuality", ms2H.getTypeCode());
        }
        int nanos = 0;
        Blockette[] blockettes = dr.getBlockettes(100);
        if (blockettes.length != 0) {
            Blockette100 b100 = (Blockette100)blockettes[0];
            ms3Header.setSampleRatePeriod(b100.getActualSampleRate());
        }
        blockettes = dr.getBlockettes(1001);
        if (blockettes.length != 0) {
            Blockette1001 b1001 = (Blockette1001)blockettes[0];
            nanos = 1000 * b1001.getMicrosecond();
            ms3Extras.put("TQ", (int)b1001.getTimingQuality());
        }
        
        if (dr.getHeader().getStartBtime().sec == 60) {
            ms3Extras.put(MSeed3Record.TIME_LEAP_SECOND, 1);
        }
        ms3Header.setNanosecond(ms3Header.getNanosecond() + nanos);
        
        if (ms3Header.nanosecond < 0) {
          ms3Header.second -= 1;
          ms3Header.nanosecond += 1000000000;
          if (ms3Header.second < 0) {
      // might be wrong for leap seconds
            ms3Header.second += 60;
            ms3Header.minute -= 1;
            if (ms3Header.minute < 0) {
              ms3Header.minute += 60;
              ms3Header.hour -= 1;
              if (ms3Header.hour < 0) {
                ms3Header.hour += 24;
                ms3Header.dayOfYear =- 1;
                if (ms3Header.dayOfYear < 0) {
      // wrong for leap years
                  ms3Header.dayOfYear += 365;
                  ms3Header.year -= 1;
                }
              }
            }
          }
        }
        ms3Header.setExtraHeaders(ms3Extras.toString());
        // need to convert if not steim1 or 2
        ms3Header.timeseriesBytes = dr.getData();
        
        
        return ms3Header;
    }
}
