package edu.sc.seis.seisFile.gcf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

    public abstract void write(DataOutput out) throws NumberFormatException, IOException;

    public static AbstractGCFBlock read(DataInput in, boolean isSerial) throws IOException, GCFFormatException {
        GCFHeader h = GCFHeader.read(in);
        try {
        if (h.getSps() != 0) {
            int fs = Utility.bytesToInt(in.readByte(), in.readByte(), in.readByte(), in.readByte(), false);
            int samp = fs;
            int[] d = new int[h.getNumPoints()];
            d[0] = samp;
            if (h.getCompression() == 1 && isSerial) {
                // three bytes per 32 bit record over serial line
                // msb dropped as it is always zero for 24 bit digitizer
                for (int i = 0; i < d.length; i++) {
                    d[i] = Utility.bytesToInt(in.readByte(), in.readByte(), in.readByte(), false);
                }
            } else if (h.getCompression() == 1 && !isSerial) {
                for (int i = 0; i < d.length; i++) {
                    d[i] = Utility.bytesToInt(in.readByte(), in.readByte(), in.readByte(), in.readByte(), false);
                }
            } else if (h.getCompression() == 2) {
                for (int i = 0; i < d.length; i++) {
                    d[i] = Utility.bytesToInt(in.readByte(), in.readByte(), false);
                }
            } else if (h.getCompression() == 4) {
                for (int i = 0; i < d.length; i++) {
                    d[i] = Utility.bytesToInt(in.readByte()); 
                }
            }
            int ls = Utility.bytesToInt(in.readByte(), in.readByte(), in.readByte(), in.readByte(), false);
            return new GCFBlock(h, d, fs, ls, isSerial);
        } else {
            byte[] statusBits = new byte[h.getNum32Records()*4];
            in.readFully(statusBits);
            return new GCFStatusBlock(h, new String(statusBits));
        }
        } catch(IOException e) {
            throw new GCFFormatException("Problem reading GCF block body. header:"+h,  e);
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
