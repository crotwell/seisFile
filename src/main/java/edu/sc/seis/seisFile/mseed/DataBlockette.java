
package edu.sc.seis.seisFile.mseed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * DataBlockette.java
 *
 *
 * Created: Thu Apr  8 12:40:56 1999
 *
 * @author Philip Crotwell
 * @version
 */
public abstract class DataBlockette extends Blockette
    implements Serializable {

    public DataBlockette(byte[] info, boolean swapBytes) {
        this.info = info;
        this.swapBytes = swapBytes;
    }

    public DataBlockette(int size) {
        this.info = new byte[size];
        System.arraycopy(Utility.intToByteArray(getType()), 2, info, 0, 2);
    }
    
    /** For use by subclasses that want to ensure that they are of a given size.
     * @throws IllegalArgumentException if the size is larger than the number of bytes
     */
    protected void trimToSize(int size) {
        if (info.length < size) {
            throw new IllegalArgumentException("Blockette "+getType()+" must have "+size+" bytes, but got "+info.length);
        }
        if (info.length > size) {
            // must be extra junk at end, trim
            byte[] tmp = new byte[size];
            System.arraycopy(info, 0, tmp, 0, size);
            info = tmp;
        }
    }
    
    public void write(DataOutputStream dos, short nextOffset) throws IOException {
        dos.write(toBytes(nextOffset));
    }

    public byte[] toBytes(short nextOffset) {
        System.arraycopy(Utility.intToByteArray(nextOffset), 2, info, 2, 2);
        return info;
    }

    public byte[] toBytes() {
        return toBytes((short)0);
    }

    protected byte[] info;
    
    protected boolean swapBytes;

} // DataBlockette
