package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;


public class Blockette1001 extends DataBlockette {

    public static final int B1001_SIZE = 8;
    
    public Blockette1001() {
        super(B1001_SIZE);
    }

    public Blockette1001(byte[] info, boolean swapBytes) throws SeedFormatException {
        super(info, swapBytes);
        trimToSize(B1001_SIZE);
    }

    public int getSize() {
        return B1001_SIZE;
    }

    public int getType() { return 1001; }

    public String getName() {
        return "Data Extension Blockette";
    }
    public byte getTimingQuality() {return info[4];}

    public void setTimingQuality(byte  v) {
        info[4] = v;
    }

    public byte getMicrosecond() {
        return info[5];
    }

    public void setMicrosecond(byte  v) {
        info[5] = v;
    }
    
    public byte getReserved() {
        return info[6];
    }

    public void setReserved(byte  v) {
        info[6] = v;
    }

    public byte getFrameCount() {return info[7];}

    public void setFrameCount(byte  v) {info[7] = v;}
    
    @Override
    public void writeASCII(PrintWriter out)  {
        out.println("Blockette1001 tQual="+getTimingQuality()+" microsec="+getMicrosecond()+" frameC="+getFrameCount());
    }
    
    
}
