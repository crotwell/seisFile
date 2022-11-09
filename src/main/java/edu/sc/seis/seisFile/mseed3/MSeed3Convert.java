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
        if (ms2H.getTypeCode() != 0 && ms2H.getTypeCode() != 'D') {
          ms3Extras.put("QI", ms2H.getTypeCode());
        }
        int nanos = 0;
        Blockette[] blockettes = dr.getBlockettes(100);
        if (blockettes.length != 0) {
            Blockette100 b100 = (Blockette100)blockettes[0];
            ms3Header.setSampleRate(b100.getActualSampleRate());
        }
        blockettes = dr.getBlockettes(100);
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

    public static void main(String[] args) throws SeedFormatException, IOException {
        boolean convert2to3 = false;
        boolean convert3to2 = false;
        boolean print3 = false;
        boolean print2 = false;
        if (args.length != 2) {
            System.err.println("Usage: MS3Convert [-2][-3][--print2][--print3] mseedfile");
            return;
        }
        if (args[0].equals("-2")) {
            convert2to3 = true;
        } else if (args[0].equals("-3")) {
            convert3to2 = true;
            System.err.println("Not impl yet");
        } else if (args[0].equals("--print3")) {
            print3 = true;
        } else if (args[0].equals("--print2")) {
            print2 = true;
            System.err.println("Not imple yet");
        } else {
            System.err.println("Don't understand " + args[0]);
            return;
        }
        File f = new File(args[1]);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
        if (convert2to3) {
            DataRecord dr2;
            File outFile = new File(f.getName() + ".ms3");
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            try {
                while ((dr2 = (DataRecord)SeedRecord.read(dis, 0)) != null) {
                    MSeed3Record ms3 = convert2to3(dr2);
                    ms3.write(dos);
                }
            } catch(EOFException e) {
                // done...
            } finally {
                if (dos != null) {
                    dos.close();
                }
            }
        } else if (print3) {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
            try {
                MSeed3Record dr3;
                int drNum = 0;
                int bytesRead = 0;
                int fileBytes = (int)f.length();
                while (bytesRead < fileBytes && (dr3 = MSeed3Record.read(dis)) != null) {
                    pw.println("--------- read record "+drNum++);
                    dr3.printASCII(pw, "  ");
                    bytesRead += dr3.getSize();
                }
                pw.println("Read "+bytesRead+" file size="+fileBytes);
            } catch(EOFException e) {
                System.err.println(e);
                e.printStackTrace();
                // done...
            } finally {
                if (pw != null) {
                    pw.flush();
                    pw.close();
                }
            }
        }
    }
}
