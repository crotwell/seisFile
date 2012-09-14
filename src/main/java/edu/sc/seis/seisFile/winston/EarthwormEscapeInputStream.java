package edu.sc.seis.seisFile.winston;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class EarthwormEscapeInputStream extends FilterInputStream {

    protected EarthwormEscapeInputStream(InputStream arg0) {
        super(arg0);
    }

    @Override
    /** careful with this as it doesn't take into account escapes. */
    public int available() throws IOException {
        // TODO Auto-generated method stub
        return super.available();
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b == EarthwormExport.ESC) {
            b = super.read();
        }
        return b;
    }

    @Override
    public int read(byte[] vals, int offset, int len) throws IOException {
        int i=0;
        while(super.available()!=0 && i < len) {
            vals[i+offset] = (byte)read();
            i++;
        }
        return i;
    }

    @Override
    public int read(byte[] vals) throws IOException {
        return read(vals, 0, vals.length);
    }

    @Override
    /** careful with this as it doesn't take into account escapes. */
    public long skip(long arg0) throws IOException {
        // TODO Auto-generated method stub
        return super.skip(arg0);
    }

}
