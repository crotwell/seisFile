package edu.sc.seis.seisFile.gcf;

import java.io.DataOutputStream;
import java.io.IOException;


public class SerialCheckSumOutputStream extends DataOutputStream {

    public SerialCheckSumOutputStream(DataOutputStream out) {
        super(out);
    }

    @Override
    public synchronized void write(byte[] b, int offset, int len) throws IOException {
        for (int i = offset; i < len; i++) {
            chksum += b[i];
        }
        super.write(b, offset, len);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        chksum += b;
        super.write(b);
    }
    
    /** writes the 2 byte checksum. Once this is written the output stream can no longer 
     * be written to.
     * @throws IOException
     */
    public short writeCheckSum() throws IOException {
        super.writeShort(chksum);
        flush();
        super.out = null;
        return chksum;
    }
    
    short chksum = 0;
}
