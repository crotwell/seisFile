package edu.sc.seis.seisFile.mseed;

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

    public abstract int getType();

    public abstract String getName();

    public abstract int getSize();

    public abstract byte[] toBytes();

    public String toString() {
        String s = getType() + ": " + getName();
        return s;
    }
}
