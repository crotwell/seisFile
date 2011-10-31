package edu.sc.seis.seisFile.sac;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;


public class SacIncrementalloader {
    
    public SacIncrementalloader(String filename) throws IOException {
        this(filename, DEFAULT_SIZE);
    }
    
    public SacIncrementalloader(String filename, int chunkSize) throws IOException {
        this.filename = filename;
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        header = new SacHeader(in);
    }
    
    public SacHeader getHeader() {
        return header;
    }
    
    public float[] next() throws IOException {
        int remaining = header.getNpts()-ptsRead;
        float[] dataSection = new float[chunkSize<remaining?chunkSize:remaining];
        if (ptsRead < header.getNpts()) {
           SacTimeSeries.readSomeData(in, dataSection, header.getByteOrder());
           ptsRead+=dataSection.length;
           return dataSection;
        }
        return new float[0];
    }
    
    public void skip(int samples) throws IOException {
        int remaining = header.getNpts()-ptsRead;
        in.skipBytes(samples<remaining?samples:remaining);
        ptsRead+=samples<remaining?samples:remaining;
    }
    
    public int getNumRemaining() {
        return header.getNpts()-ptsRead;
    }
    
    public int getNumRead() {
        return ptsRead;
    }
    
    DataInput in;
    SacHeader header;
    int chunkSize;
    int ptsRead = 0;
    String filename;
    
    public static final int DEFAULT_SIZE = 10000;
    
}
