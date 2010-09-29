package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.Writer;


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

    public void writeASCII(Writer out) throws IOException {
        out.write("Blockette UNKNOWN: "+getType());
    }
    
    public boolean getSwapBytes() {
        return swapBytes;
    }

    protected int type;

}
