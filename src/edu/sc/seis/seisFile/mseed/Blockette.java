package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.Writer;

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
     * Method writeASCII
     * 
     * @param out
     *            a Writer
     * 
     */
    public abstract void writeASCII(Writer out) throws IOException;

    public static Blockette parseBlockette(int type, byte[] bytes)
            throws IOException, SeedFormatException {
        switch(type){
            case 100:
                return new Blockette100(bytes);
            case 200:
                return new Blockette200(bytes);
            case 1000:
                return new Blockette1000(bytes);
            default:
                return new BlocketteUnknown(bytes, type);
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
