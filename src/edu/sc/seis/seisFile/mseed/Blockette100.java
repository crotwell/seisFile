/**
 * Blockette100.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.seisFile.mseed;
import java.io.IOException;
import java.io.Writer;



public class Blockette100  extends  DataBlockette {

    public Blockette100() {
        super(B100_SIZE);
    }

    public Blockette100(byte[] info) {
        super(info);
        trimToSize(B100_SIZE);
    }

    /**
     * Sets ActualSampleRate
     *
     * @param    ActualSampleRate    a  float
     */
    public void setActualSampleRate(float actualSampleRate) {
        int bits = Float.floatToIntBits(actualSampleRate);
        byte[] b = Utility.intToByteArray(bits);
        System.arraycopy(b, 0, info, 4, 4);
    }

    /**
     * Returns ActualSampleRate
     *
     * @return    a  float
     */
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

    public void writeASCII(Writer out) throws IOException {
        out.write("Blockette100 "+getActualSampleRate());
    }

    public static final int B100_SIZE = 12;
}

