package edu.sc.seis.seisFile.mseed;

import java.io.IOException;


public interface BlocketteFactory {
    
    public Blockette parseBlockette(int type, byte[] bytes, boolean swapBytes)
    throws IOException, SeedFormatException;
    
}
