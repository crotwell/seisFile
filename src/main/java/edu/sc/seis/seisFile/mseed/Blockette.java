package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Superclass of all seed blockettes. The actual blockettes do not store either
 * their blockette type or their length in the case of ascii blockettes or next
 * blockettes offset in the case of data blockettes as these are either already
 * known (ie type) or may change after reading due to data changes. Instead each
 * of these values are calculated based on the data.
 */
public abstract class Blockette {

    public Blockette() {}

    /**
     * Writes an ASCII version of the blockette. This is not meant to be a definitive ascii representation,
     * merely to give something to print for debugging purposes. Ideally each field of each blockette should
     * be printed in the order they appear in the blockette in a visually appealing way.
     * 
     * @param out
     *            a Writer
     * 
     */
    public abstract void writeASCII(PrintWriter out) ;

    public void writeASCII(PrintWriter out, String indent) {
        out.write(indent);
        writeASCII(out);
    }

    public static Blockette parseBlockette(int type, byte[] bytes, boolean swapBytes)
            throws IOException, SeedFormatException {
        switch(type){
            case 5:
                return new Blockette5(bytes);
            case 8:
                return new Blockette8(bytes);
            case 10:
                return new Blockette10(bytes);
            case 100:
                return new Blockette100(bytes, swapBytes);
            case 200:
                return new Blockette200(bytes, swapBytes);
            case 1000:
                return new Blockette1000(bytes, swapBytes);
            case 1001:
                return new Blockette1001(bytes, swapBytes);
            case 2000:
                return new Blockette2000(bytes, swapBytes);
            default:
                if (type < 100) {
                    return new BlocketteUnknown(bytes, type, swapBytes);
                } else {
                    return new DataBlocketteUnknown(bytes, type, swapBytes);
                }
        }
    }

    public abstract int getType();

    public abstract String getName();

    public abstract int getSize();

    public abstract byte[] toBytes();

    public String toString() {
        String s = getType() + ": " + getName();
        return s;
    }
}
