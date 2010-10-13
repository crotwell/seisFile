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

    public void writeASCII(PrintWriter out) throws IOException {
        String infoStr = new String(info);
        out.print("Partial Blockette "+getType()+", "+bytesRead+" of "+bytesRead+"+"+priorBytes+" of "+totalBytes+" bytes: "+infoStr);
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
