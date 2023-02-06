package edu.sc.seis.seisFile.sac;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Incrementally read data from large SAC files to avoid reading entire file into memory at once.
 */
public class SacIncrementalloader {
    
    public SacIncrementalloader(String filename) throws IOException {
        this(filename, DEFAULT_SIZE);
    }
    
    public SacIncrementalloader(String filename, int chunkSize) throws IOException {
        this(new File(filename), chunkSize);
    }

    public SacIncrementalloader(File file) throws IOException {
        this(file, DEFAULT_SIZE);
    }

    public SacIncrementalloader(File file, int chunkSize) throws IOException {
        this(new DataInputStream(new BufferedInputStream(new FileInputStream(file))), chunkSize);
    }
    
    public SacIncrementalloader(DataInput in, int chunkSize) throws IOException {
        this.in = in;
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
    
    public void close() {
        if (in != null) {
            if (in instanceof DataInputStream) {
                try {
                    ((DataInputStream)in).close();
                } catch (IOException e) {
                    // oh well...
                }
            }
        }
        in = null;
    }
    
    DataInput in;
    SacHeader header;
    int chunkSize;
    int ptsRead = 0;
    String filename;
    File file;
    
    public static final int DEFAULT_SIZE = 10000;
    
}
