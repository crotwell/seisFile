package edu.sc.seis.seisFile.gcf;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class GCFBlock extends AbstractGCFBlock {

    public GCFBlock(GCFHeader header, int[] diffData, int firstSample, int lastSample, boolean isSerial) {
        super(header);
        this.diffData = diffData;
        this.firstSample = firstSample;
        this.lastSample = lastSample;
        this.isSerial = isSerial;
    }

    public int getSize() {
        int size = 24;
        if (isSerial && header.compression == 1) {
            size += diffData.length * 3;
        } else {
            size += diffData.length * 4 / header.compression;
        }
        return size;
    }

    public int[] getDiffData() {
        return diffData;
    }

    public int getFirstSample() {
        return firstSample;
    }

    public int getLastSample() {
        return lastSample;
    }

    public int[] getUndiffData() {
        int[] out = new int[getHeader().getNumPoints()];
        out[0] = getFirstSample();
        for (int i = 1; i < out.length; i++) {
            out[i] = out[i - 1] + diffData[i];
        }
        if (out[out.length-1] != getLastSample()) {
            logger.error("bad last data point match: expected "+getLastSample()+" but was "+out[out.length-1]);
            logger.error("first: "+getFirstSample());
            for (int i = 0; i < diffData.length; i++) {
                logger.error(i+" "+diffData[i]+" "+out[i]);
            }
            
        }
        return out;
    }

    public void write(DataOutput out) throws NumberFormatException, IOException {
        header.write(out);
        int diff = 0;
        out.writeInt(firstSample);
        if (header.getCompression() == 1 && isSerial) {
            // three bytes per 32 bit record over serial line
            // msb dropped as it is always zero for 24 bit digitizer
            out.write(0);
            out.writeShort(0);
            for (int i = 1; i < diffData.length; i++) {
                diff = diffData[i] - diffData[i - 1];
                out.write((diff & 0xff0000) >> 16);
                out.writeShort((short)(diff & 0xffff));
            }
        } else if (header.getCompression() == 1 && !isSerial) {
            out.writeInt(0);
            for (int i = 1; i < diffData.length; i++) {
                diff = diffData[i] - diffData[i - 1];
                out.writeInt(diff);
            }
        } else if (header.getCompression() == 2) {
            for (int i = 1; i < diffData.length; i++) {
                diff = diffData[i] - diffData[i - 1];
                out.writeShort((short)(diff & 0xffff));
            }
        } else if (header.getCompression() == 4) {
            for (int i = 1; i < diffData.length; i++) {
                diff = diffData[i] - diffData[i - 1];
                out.write((byte)diff);
            }
        }
        out.writeInt(lastSample);
    }

    public static GCFBlock mockGCF(Date startTime, int[] data, boolean isSerial) {
        int[] daySec = Convert.convertTime(startTime);
        GCFHeader h = new GCFHeader(MOCK_SYSID, MOCK_STREAMID, daySec[0], daySec[1], 100, 1, data.length);
        GCFBlock block = new GCFBlock(h, data, data[0], data[data.length - 1], isSerial);
        return block;
    }

    int[] diffData;

    int firstSample;

    int lastSample;

    boolean isSerial;

    public static final String MOCK_SYSID = "WO1234";

    public static final String MOCK_STREAMID = "4321Z0";

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(diffData);
        result = prime * result + firstSample;
        result = prime * result + (isSerial ? 1231 : 1237);
        result = prime * result + lastSample;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        GCFBlock other = (GCFBlock)obj;
        if (!Arrays.equals(diffData, other.diffData))
            return false;
        if (firstSample != other.firstSample)
            return false;
        if (isSerial != other.isSerial)
            return false;
        if (lastSample != other.lastSample)
            return false;
        return true;
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GCFBlock.class);

}
