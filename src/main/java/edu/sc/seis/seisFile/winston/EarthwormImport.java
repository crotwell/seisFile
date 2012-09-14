package edu.sc.seis.seisFile.winston;

import java.io.IOException;


public class EarthwormImport {
    
    public EarthwormImport(EarthwormEscapeInputStream in) {
        this.in = in;
    }
    
    public EarthwormMessage nextMessage() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int index = 0;
        int nextByte = in.read();
        while(nextByte != EarthwormExport.STX) {
            nextByte = in.read();
        }
        while(nextByte != EarthwormExport.ETX) {
            buffer[index] = (byte)nextByte;
            index++;
            nextByte = in.read();
        }
        byte[] tbBuf = new byte[index];
        System.arraycopy(buffer, 0, tbBuf, 0, tbBuf.length);
        return new EarthwormMessage(tbBuf);
    }
    
    EarthwormEscapeInputStream in;
    
    int BUFFER_SIZE = 4096*2;
}
