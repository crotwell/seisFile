package edu.sc.seis.seisFile.mseed;

/**
 * BlocketteUnknown.java
 * 
 * 
 * Created: Mon Apr 5 15:48:51 1999
 * 
 * @author Philip Crotwell
 * @version
 */
import java.io.IOException;
import java.io.PrintWriter;

public class BlocketteUnknown extends Blockette {

    public BlocketteUnknown(byte[] info, int type, boolean swapBytes) {
        this.info = info;
        this.type = type;
        this.swapBytes = swapBytes;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return "Unknown";
    }

    public int getSize() {
        return info.length;
    }

    public byte[] toBytes() {
        return info;
    }

    public void writeASCII(PrintWriter out) throws IOException {
        out.write("Blockette UNKNOWN"+getType());
    }
    
    public boolean getSwapBytes() {
        return swapBytes;
    }

    protected int type;

    protected byte[] info;
    
    protected boolean swapBytes;
} // BlocketteUnknown
