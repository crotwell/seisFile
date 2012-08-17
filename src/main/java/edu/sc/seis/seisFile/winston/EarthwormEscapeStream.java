package edu.sc.seis.seisFile.winston;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EarthwormEscapeStream extends FilterOutputStream {

    public EarthwormEscapeStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        if (b == EarthwormExport.ESC || b == EarthwormExport.ETX) {
            super.write(EarthwormExport.ESC);
        }
        super.write(b);
    }
    
    /** not efficient, but...*/
    @Override
    public void write(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++) {
            write(b[i]);
        }
    }
    
    /** not efficient, but...*/
    @Override
    public void write(byte[] b, int offset, int len) throws IOException {
        for (int i = offset; i < len && i < b.length; i++) {
            write(b[i]);
        }
    }
    
    public void startTransmit() throws IOException {
        super.write(EarthwormExport.STX);
    }
    
    public void endTransmit() throws IOException {
        super.write(EarthwormExport.ETX);
    }
    
}
