package edu.sc.seis.seisFile.gcf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
        return out;
    }

    public void write(DataOutputStream out) throws NumberFormatException, IOException {
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
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(startTime);
        int dayNum = 365 * (cal.get(Calendar.YEAR) - 1989) + 13 + 31 + 6 + cal.get(Calendar.DAY_OF_YEAR);
        int secOfDay = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)
                + 1;
        GCFHeader h = new GCFHeader(MOCK_SYSID, MOCK_STREAMID, dayNum, secOfDay, 100, 1, data.length);
        GCFBlock block = new GCFBlock(h, data, data[0], data[data.length - 1], isSerial);
        return block;
    }

    int[] diffData;

    int firstSample;

    int lastSample;

    boolean isSerial;

    public static final String MOCK_SYSID = "W1234";

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
}
