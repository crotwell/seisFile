package edu.sc.seis.seisFile.mseed;

/**
 * SeedRecord.java
 * 
 * 
 * Created: Thu Apr 8 11:54:07 1999
 * 
 * @author Philip Crotwell
 * @version
 */
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class SeedRecord {

    public static SeedRecord read(DataInputStream inStream) throws IOException, SeedFormatException {
        return read(inStream, 0);
    }

    public static SeedRecord read(byte[] bytes) throws IOException, SeedFormatException {
        DataInputStream seedIn = new DataInputStream(new ByteArrayInputStream(bytes));
        return DataRecord.read(seedIn);
        
    }

    /**
     * allows setting of a default record size, making reading of miniseed that
     * lack a Blockette1000. Compression is still unknown, but at least the
     * record can be read in and manipulated. A value of 0 for defaultRecordSize
     * means there must be a blockette 1000 or a MissingBlockette1000 will be
     * thrown.
     * 
     * If an exception is thrown and the underlying stream supports it, the stream
     * will be reset to its state prior to any bytes being read. The buffer in the
     * underlying stream must be large enough buffer any values read prior to the
     * exception. A buffer sized to be the largest seed record expected is sufficient
     * and so 4096 is a reasonable buffer size.
     */
    public static SeedRecord read(DataInput inStream, int defaultRecordSize) throws IOException, SeedFormatException {
        boolean resetOnError = inStream instanceof DataInputStream && ((InputStream)inStream).markSupported();
        if(resetOnError) {
            ((InputStream)inStream).mark(4096);
        }
        try {
            ControlHeader header = ControlHeader.read(inStream);
            SeedRecord newRecord;
            if(header instanceof DataHeader) {
                newRecord = DataRecord.readDataRecord(inStream,
                                      (DataHeader)header,
                                      defaultRecordSize);
            } else {
                ControlRecord contRec =  ControlRecord.readControlRecord(inStream,
                                      header,
                                      defaultRecordSize, priorRecord);
                defaultRecordSize = contRec.getRecordSize(); // in case of b8 or b5 setting record size
                newRecord = contRec;
            }
            priorRecord = newRecord;
            return priorRecord;
        } catch(SeedFormatException e) {
            if(resetOnError) {
                ((InputStream)inStream).reset();
            }
            throw e;
        } catch(IOException e) {
            if(resetOnError) {
                ((InputStream)inStream).reset();
            }
            throw e;
        } catch(RuntimeException e) {
            if(resetOnError) {
                ((InputStream)inStream).reset();
            }
            throw e;
        }
    }
    
    protected static SeedRecord priorRecord = null;

    public SeedRecord(ControlHeader header) {
        this.header = header;
    }

    public void addBlockette(Blockette b) throws SeedFormatException {
        blockettes.add(b);
    }

    public Blockette[] getBlockettes() {
        return blockettes.toArray(new Blockette[0]);
    }

    public Blockette getUniqueBlockette(int type) throws SeedFormatException {
        Blockette[] b = getBlockettes(type);
        if (b.length == 1) {
            return b[0];
        } else if (b.length == 0) {
            if (type == 1000) {
                // special case as b1000 is required in mseed
                throw new MissingBlockette1000();
            }
            throw new SeedFormatException("No blockettes of type "+type);
        } else {
            throw new SeedFormatException("Multiple blockettes of type "+type);
        }
    }

    public int getNumBlockettes(int type) throws SeedFormatException {
        int out = 0;
        for (Blockette b : blockettes) {
            if(b.getType() == type) {
                out++;
            }
        }
        return out;
    }

    public Blockette[] getBlockettes(int type) {
        List<Blockette> out = new ArrayList<Blockette>();
        for (Blockette b : blockettes) {
            if(b.getType() == type) {
                out.add(b);
            }
        }
        return out.toArray(new Blockette[0]);
    }
    
    /** 
     * if a seed blockette is continued in this record, a PartialBlockette will
     * exist here. It will know its type and length, but will not have all its needed
     * bytes. The prior Seed Record, possibly with reading the subsequent Seed Record should allow the remaining portion
     * of the data to be read. This returns null in the case of no first partial blockette
     * existing.
     */
    public PartialBlockette getFirstPartialBlockette() {
        if (blockettes.get(0) instanceof PartialBlockette) {
            return (PartialBlockette)blockettes.get(0);
        }
        return null;
    }
    
    /** 
     * if a seed blockette is continued in the next record, a PartialBlockette will
     * exist here. It will know its type and length, but will not have all its needed
     * bytes. Reading the subsequent Seed Record should allow the remaining portion
     * of the data to be read. This returns null in the case of no partial blockette
     * existing.
     */
    public PartialBlockette getLastPartialBlockette() {
        if (blockettes.get(blockettes.size()-1) instanceof PartialBlockette) {
            return (PartialBlockette)blockettes.get(blockettes.size()-1);
        }
        return null;
    }
    
    public ControlHeader getControlHeader() {
        return header;
    }

    public String toString() {
        String s = "Record for " + header + "\n";
        s += "Blockettes:\n";
        for(int i = 0; i < blockettes.size(); i++) {
            s += blockettes.get(i) + "\n";
        }
        return s;
    }

    public void writeASCII(PrintWriter out) throws IOException {
        writeASCII(out, "");
    }
    
    public void writeASCII(PrintWriter out, String indent) throws IOException {
        if (this instanceof DataRecord) {
            out.print(indent+"DataRecord");
        } else if (this instanceof ControlRecord) {
            out.print(indent+"ControlRecord");
        } else {
            out.print(indent+"SeedRecord");
        }
        getControlHeader().writeASCII(out, indent+"  ");
        for (Blockette b : blockettes) {
            b.writeASCII(out, indent+"    ");
            out.println();
        }
    }
    
    public int getRecordSize() {
        return RECORD_SIZE;
    }

    protected ControlHeader header;

    protected List<Blockette> blockettes = new ArrayList<Blockette>();

    protected int RECORD_SIZE = 4096;
} // SeedRecord
