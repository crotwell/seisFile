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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @deprecated See ListHeader for an example client and SeedRecord.read for reading
 * 
 * @author crotwell
 *
 */
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
    public SeedRecord getNextRecord() throws SeedFormatException, IOException {
        return getNextRecord(0);
    }
    /**
     * gets the next logical record int the seed volume. This may not exactly
     * correspond to the logical record structure within the volume as
     * "continued" records will be concatinated to avoid partial blockettes.
     */
    public SeedRecord getNextRecord(int defaultRecordSize) throws SeedFormatException, IOException {
        return SeedRecord.read(inStream, defaultRecordSize);
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
        PrintWriter out = new PrintWriter(System.out, true);
        int maxPackets = -1;
        try {
            out.println("open socket");
            if(args.length == 0) {
                out.println("Usage: java "+MiniSeedRead.class.getName()+" filename");
            } else {
                ls = new DataInputStream(new BufferedInputStream(new FileInputStream(args[0]), 4096));
            }
            MiniSeedRead rf = new MiniSeedRead(ls);
            for(int i = 0; maxPackets == -1 || i < maxPackets; i++) {
                SeedRecord sr;
                try {
                    sr = rf.getNextRecord();
                } catch(MissingBlockette1000 e) {
                    out.println("Missing Blockette1000, trying with record size of 4096");
                    // try with 4096 as default
                    sr = rf.getNextRecord(4096);
                }
                sr.writeASCII(out, "    ");
                if(sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord)sr;
                    byte[] data = dr.getData();
                    // should use seedCodec to do something with the data...
                }
            }
        } catch(EOFException e) {
            System.out.println("EOF, so done.");
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
