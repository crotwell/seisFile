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

import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class MSeed3Convert {

    public MSeed3Convert() {
        // TODO Auto-generated constructor stub
    }

    public static MSeed3Record convert2to3(DataRecord dr) throws SeedFormatException {
        MSeed3Header header = new MSeed3Header();
        header.setNetworkCode(dr.getHeader().getNetworkCode());
        header.setStationCode(dr.getHeader().getStationIdentifier());
        header.setLocationCode(dr.getHeader().getLocationIdentifier());
        header.setChannelCode(dr.getHeader().getChannelIdentifier());
        header.setQualityIndicator(dr.getHeader().getQualityIndicator());
        header.setDataVersion((byte)0);
        header.setRecordLength(dr.getRecordSize());
        long millis = dr.getHeader().getStartBtime().convertToCalendar().getTimeInMillis();
        int micros = 0;
        Blockette1001 b1001;
        try {
            b1001 = (Blockette1001)dr.getUniqueBlockette(1001);
            micros = b1001.getMicrosecond();
        } catch(SeedFormatException e) {
            // oh well, no b1001 so no micros
        }
        header.setStartTime(millis * 1000 + micros);
        header.setNumSamples(dr.getHeader().getNumSamples());
        float sampleRate = dr.getHeader().getSampleRate();
        if (sampleRate < 1) {
            sampleRate = -1 / sampleRate; // means really period in sec
        }
        header.setSampleRate(sampleRate);
        header.setDataCRC(0); // should calc this...would need to decomp data
        header.setFlags((byte)0x1); // should extract...
        Blockette1000 b1000 = (Blockette1000)dr.getUniqueBlockette(1000);
        header.setDataEncodingFormat(b1000.getEncodingFormat());
        header.setNumOpaqueHeaders((byte)0);
        header.setOpaqueHeaders(new String[0]);
        MSeed3Record out = new MSeed3Record(header, dr.getData());
        
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
        try {
            out.getHeader().writeASCII(pw, "  ");
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pw.flush();
        return out;
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
            System.err.println("Not imple yet");
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
                    dr3.getHeader().writeASCII(pw, "  ");
                    bytesRead += dr3.getHeader().getRecordLength();
                }
                pw.println("Read "+bytesRead+" file size="+fileBytes);
            } catch(EOFException e) {
                System.err.println("EOF");
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
