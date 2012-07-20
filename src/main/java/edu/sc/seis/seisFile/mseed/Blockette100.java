/**
 * Blockette100.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;

public class Blockette100 extends DataBlockette {

    public Blockette100() {
        super(B100_SIZE);
    }

    public Blockette100(byte[] info, boolean swapBytes) throws SeedFormatException {
        super(info, swapBytes);
        trimToSize(B100_SIZE);
    }

    public void setActualSampleRate(float actualSampleRate) {
        Utility.insertFloat(actualSampleRate, info, 4);
    }

    public float getActualSampleRate() {
        int bits = Utility.bytesToInt(info[4], info[5], info[6], info[7], false);
        return Float.intBitsToFloat(bits);
    }

    public int getType() {
        return 100;
    }

    public int getSize() {
        return B100_SIZE;
    }

    public String getName() {
        return "Sample Rate Blockette";
    }

    public void writeASCII(PrintWriter out) {
        out.println("Blockette100 " + getActualSampleRate());
    }

    public static final int B100_SIZE = 12;
}
