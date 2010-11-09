package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;


public class DataBlocketteUnknown extends DataBlockette {

    public DataBlocketteUnknown(byte[] info, int type, boolean swapBytes) {
        super(info, swapBytes);
        this.type = type;
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
        out.println("Blockette: "+getType());
    }
    
    public boolean getSwapBytes() {
        return swapBytes;
    }

    protected int type;

}
