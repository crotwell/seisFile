package edu.sc.seis.seisFile.winston;

import java.io.IOException;
import java.io.InputStream;


public class EarthwormImport {
    
    public EarthwormImport(InputStream in) {
        this.in = in;
    }
    
    public EarthwormMessage nextMessage() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int index = 0;
        int nextByte = in.read();
        boolean isEscapedByte = false;
        while(nextByte != EarthwormExport.STX) {
            nextByte = in.read();
        }
        // got a start, now read data
        nextByte = in.read();
        if (nextByte == EarthwormExport.ESC) {
            isEscapedByte = true;
            nextByte = in.read();
        }
        
        while( isEscapedByte || nextByte != EarthwormExport.ETX) {
            buffer[index] = (byte)nextByte;
            index++;
            isEscapedByte = false;
            nextByte = in.read();
            if (nextByte == EarthwormExport.ESC) {
                isEscapedByte = true;
                nextByte = in.read();
            }
        }
        byte[] tbBuf = new byte[index];
        System.arraycopy(buffer, 0, tbBuf, 0, tbBuf.length);
        return new EarthwormMessage(tbBuf);
    }
    
    InputStream in;
    
    int BUFFER_SIZE = 4096*2;
}
