package edu.sc.seis.seisFile.gcf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.iris.dmc.seedcodec.Utility;

public abstract class AbstractGCFBlock {

    protected AbstractGCFBlock(GCFHeader header) {
        this.header = header;
    }

    GCFHeader header;

    public GCFHeader getHeader() {
        return header;
    }

    public abstract int getSize();

    public abstract void write(DataOutputStream out) throws NumberFormatException, IOException;

    public static AbstractGCFBlock read(InputStream in, boolean isSerial) throws IOException {
        GCFHeader h = GCFHeader.read(in);
        if (h.getSps() != 0) {
            int fs = Utility.bytesToInt((byte)in.read(), (byte)in.read(), (byte)in.read(), (byte)in.read(), false);
            int samp = fs;
            int[] d = new int[h.getNumPoints()];
            d[0] = samp;
            int diff;
            if (h.getCompression() == 1 && isSerial) {
                // three bytes per 32 bit record over serial line
                // msb dropped as it is always zero for 24 bit digitizer
                for (int i = 0; i < d.length; i++) {
                    diff = Utility.bytesToInt((byte)in.read(), (byte)in.read(), (byte)in.read(), false);
                    samp += diff;
                    d[i] = samp;
                }
            } else if (h.getCompression() == 1 && !isSerial) {
                for (int i = 0; i < d.length; i++) {
                    diff = Utility.bytesToInt((byte)in.read(), (byte)in.read(), (byte)in.read(), (byte)in.read(), false);
                    samp += diff;
                    d[i] = samp;
                }
            } else if (h.getCompression() == 2) {
                for (int i = 0; i < d.length; i++) {
                    diff = Utility.bytesToInt((byte)in.read(), (byte)in.read(), false);
                    samp += diff;
                    d[i] = samp;
                }
            } else if (h.getCompression() == 4) {
                for (int i = 0; i < d.length; i++) {
                    diff = in.read();
                    samp += diff;
                    d[i] = samp;
                }
            }
            int ls = Utility.bytesToInt((byte)in.read(), (byte)in.read(), (byte)in.read(), (byte)in.read(), false);
            return new GCFBlock(h, d, fs, ls, isSerial);
        } else {
            byte[] statusBits = new byte[h.getNum32Records()*4];
            int offset = 0;
            while (offset < statusBits.length) {
                in.read(statusBits, offset, statusBits.length-offset);
            }
            return new GCFStatusBlock(h, new String(statusBits));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractGCFBlock other = (AbstractGCFBlock)obj;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        return true;
    }
}
