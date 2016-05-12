package edu.sc.seis.seisFile.mseed3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class MSeed3Convert {

    public MSeed3Convert() {
        // TODO Auto-generated constructor stub
    }
    
    public static MSeed3Header convert2to3(DataRecord dr) throws SeedFormatException {
        MSeed3Header out = new MSeed3Header();
        out.setNetworkCode(dr.getHeader().getNetworkCode());
        out.setStationCode(dr.getHeader().getStationIdentifier());
        out.setLocationCode(dr.getHeader().getLocationIdentifier());
        out.setChannelCode(dr.getHeader().getChannelIdentifier());
        out.setQualityIndicator(dr.getHeader().getQualityIndicator());
        out.setDataVersion((byte)0);
        out.setRecordLength(dr.getRecordSize());
        long millis = dr.getHeader().getStartBtime().convertToCalendar().getTimeInMillis();
        int micros = 0;
            Blockette1001 b1001;
            try {
                b1001 = (Blockette1001)dr.getUniqueBlockette(1001);
                micros = b1001.getMicrosecond();
            } catch(SeedFormatException e) {
                // oh well, no b1001 so no micros
            }
        out.setStartTime(millis*1000+micros);
        out.setNumSamples(dr.getHeader().getNumSamples());
        float sampleRate = dr.getHeader().getSampleRate();
        if (sampleRate < 1) {
            sampleRate = -1/sampleRate; // means really period in sec
        }
        out.setSampleRate(sampleRate);
        out.setDataCRC(0);   // should calc this...would need to decomp data
        out.setFlags((byte)0x0);  // should extract...
        Blockette1000 b1000 = (Blockette1000)dr.getUniqueBlockette(1000);
        out.setDataEncodingFormat(b1000.getEncodingFormat());
        out.setNumOpaqueHeaders((byte)0);
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
        } else if (args[0].equals("--print3")) {
            print3 = true;
        } else if (args[0].equals("--print2")) {
            print2 = true;
        } else {
            System.err.println("Don't understand " + args[0]);
            return;
        }

        File f = new File(args[1]);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
        if (convert2to3) {
            DataRecord dr2 = (DataRecord)DataRecord.read(dis);
        }
        MSeed3Header dh;
    }
}
