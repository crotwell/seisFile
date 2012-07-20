package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;


public class PartialBlockette extends BlocketteUnknown {

    public PartialBlockette(int type, byte[] info, boolean swapBytes, int priorBytes, int totalBytes) {
        super(info, type, swapBytes);
        bytesRead = info.length;
        this.priorBytes = priorBytes;
        this.totalBytes = totalBytes;
    }

    public static PartialBlockette combine(PartialBlockette first, PartialBlockette second) {
        byte[] tmp = new byte[first.getSize()+second.getSize()];
        System.arraycopy(first.toBytes(), 0, tmp, 0, first.getSize());
        System.arraycopy(second.toBytes(), 0, tmp, first.getSize(), second.getSize());
        return new PartialBlockette(first.getType(), tmp, first.swapBytes, first.getPriorSize(), first.getTotalSize());
    }
    
    public void writeASCII(PrintWriter out) {
        String infoStr = new String(info);
        out.println("Partial Blockette "+getType()+", "+bytesRead+" with "+priorBytes+" prior of "+totalBytes+" total bytes: "+infoStr);
    }

    public boolean isBegin() {
        return priorBytes == 0;
    }
    
    public boolean isEnd() {
        return priorBytes+bytesRead == totalBytes;
    }
    
    public int getTotalSize() {
        return totalBytes;
    }
    
    public int getPriorSize() {
        return priorBytes;
    }
    
    public int getSoFarSize() {
        return priorBytes+bytesRead;
    }

    int totalBytes;
    
    int priorBytes;
    
    int bytesRead;

    public int getBytesRead() {
        return bytesRead;
    }
}
