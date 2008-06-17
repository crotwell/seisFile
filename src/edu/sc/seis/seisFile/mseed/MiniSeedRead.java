package edu.sc.seis.seisFile.mseed;

/**
 * MiniSeedRead.java
 * 
 * 
 * Created: Thu Apr 8 12:10:52 1999
 * 
 * @author Philip Crotwell
 * @version
 */
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class MiniSeedRead {

    protected MiniSeedRead() {}

    public MiniSeedRead(DataInput inStream) throws IOException {
        this.inStream = inStream;
    }

    public void close() throws IOException {
        inStream = null;
    }

    /**
     * gets the next logical record int the seed volume. This may not exactly
     * correspond to the logical record structure within the volume as
     * "continued" records will be concatinated to avoid partial blockettes.
     */
    public DataRecord getNextRecord() throws SeedFormatException, IOException {
        return getNextRecord(0);
    }
    /**
     * gets the next logical record int the seed volume. This may not exactly
     * correspond to the logical record structure within the volume as
     * "continued" records will be concatinated to avoid partial blockettes.
     */
    public DataRecord getNextRecord(int defaultRecordSize) throws SeedFormatException, IOException {
        return DataRecord.read(inStream, defaultRecordSize);
    }

    public int getNumRecordsRead() {
        return numRead;
    }

    protected int numRead = 0;

    protected DataInput inStream;

    protected int recordSize;

    protected boolean readData;

    public static void main(String[] args) {
        DataInputStream ls = null;
        try {
            System.out.println("open socket");
            if(args.length == 0) {
                Socket lissConnect = new Socket("anmo.iu.liss.org", 4000);
                ls = new DataInputStream(new BufferedInputStream(lissConnect.getInputStream(),
                                                                 1024));
            } else {
                ls = new DataInputStream(new BufferedInputStream(new FileInputStream(args[0]), 4096));
            }
            MiniSeedRead rf = new MiniSeedRead(ls);
            for(int i = 0; i < 10; i++) {
                SeedRecord sr;
                try {
                    sr = rf.getNextRecord();
                } catch(MissingBlockette1000 e) {
                    System.out.println("Missing Blockette1000, trying with record size of 4096");
                    // try with 4096 as default
                    sr = rf.getNextRecord(4096);
                }
                System.out.println(sr);
                if(sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord)sr;
                    byte[] data = dr.getData();
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                if(ls != null)
                    ls.close();
            } catch(Exception ee) {}
        }
    }
} // MiniSeedRead
