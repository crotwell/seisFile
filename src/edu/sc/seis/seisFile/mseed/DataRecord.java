package edu.sc.seis.seisFile.mseed;

/**
 * DataRecord.java
 * 
 * 
 * Created: Thu Apr 8 13:52:27 1999
 * 
 * @author Philip Crotwell
 * @version
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

public class DataRecord extends SeedRecord implements Serializable {

    public DataRecord(DataHeader header) {
        super(header);
    }

    public void addBlockette(Blockette b) throws SeedFormatException {
        if(b == null) {
            throw new IllegalArgumentException("Blockette cannot be null");
        }
        if(b instanceof DataBlockette) {
            super.addBlockette(b);
            getHeader().setNumBlockettes((byte)(getHeader().getNumBlockettes() + 1));
        } else if(b instanceof BlocketteUnknown) {
            System.out.println("BlockettUnknown added: " + b.getType());
        } else {
            throw new SeedFormatException("Cannot add non-data blockettes to a DataRecord "
                    + b.getType());
        }
        recheckDataOffset();
    }

    protected void recheckDataOffset() throws SeedFormatException {
        int size = getHeader().getSize();
        Blockette[] blocks = getBlockettes();
        for(int i = 0; i < blocks.length; i++) {
            size += blocks[i].getSize();
        }
        if(data != null) {
            size += data.length;
        }
        if(size > RECORD_SIZE) {
            throw new SeedFormatException("Can't fit blockettes and data in record "
                    + size);
        }
        if(data != null) {
            // shift the data to end of blockette so pad happens between
            // blockettes and data
            getHeader().setDataOffset((short)(RECORD_SIZE - data.length));
        }
    }

    /**
     * returns the data from this data header unparsed, is as a byte array in
     * the format from blockette 1000. The return type is byte[], so the caller
     * must decode the data based on its format.
     */
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) throws SeedFormatException {
        this.data = data;
        recheckDataOffset();
    }

    public int getDataSize() {
        return data.length;
    }

    public DataHeader getHeader() {
        return (DataHeader)header;
    }

    public void write(DataOutputStream dos) throws IOException {
        Blockette[] blocks = getBlockettes();
        getHeader().setNumBlockettes((byte)blocks.length);
        if(blocks.length != 0) {
            getHeader().setDataBlocketteOffset((byte)48);
        }
        getHeader().write(dos);
        DataBlockette dataB;
        short blockettesSize = getHeader().getSize();
        for(int i = 0; i < blocks.length; i++) {
            dataB = (DataBlockette)blocks[i];
            blockettesSize += (short)dataB.getSize();
            if(i != blocks.length - 1) {
                dos.write(dataB.toBytes(blockettesSize));
            } else {
                dos.write(dataB.toBytes((short)0));
            }
        } // end of for ()
        for(int i = blockettesSize; i < getHeader().getDataOffset(); i++) {
            dos.write(ZERO_BYTE);
        }
        dos.write(data);
        int remainBytes = RECORD_SIZE - getHeader().getDataOffset()
                - data.length;
        for(int i = 0; i < remainBytes; i++) {
            dos.write(ZERO_BYTE);
        } // end of for ()
    }

    public static DataRecord read(DataInputStream inStream) throws IOException,
            SeedFormatException {
        ControlHeader header = ControlHeader.read(inStream);
        if(header instanceof DataHeader) {
            return readDataRecord(inStream, (DataHeader)header);
        } else {
            throw new SeedFormatException("Found a control header in a miniseed file");
        }
    }

    protected static DataRecord readDataRecord(DataInputStream inStream,
                                               DataHeader header)
            throws IOException, SeedFormatException {
        /*
         * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
         * "Offset to first blockette must be larger than the header size");
         */
        byte[] garbage = new byte[header.getDataBlocketteOffset()
                - header.getSize()];
        DataRecord dataRec = new DataRecord(header);
        if(garbage.length != 0) {
            inStream.readFully(garbage);
        }
        byte[] blocketteBytes;
        int currOffset = header.getDataBlocketteOffset();
        int type, nextOffset;
        for(int i = 0; i < header.getNumBlockettes(); i++) {
            // get blockette type (first 2 bytes)
            byte hibyteType = inStream.readByte();
            byte lowbyteType = inStream.readByte();
            type = Utility.uBytesToInt(hibyteType, lowbyteType, false);
            // System.out.println("Blockette type "+type);
            byte hibyteOffset = inStream.readByte();
            byte lowbyteOffset = inStream.readByte();
            nextOffset = Utility.uBytesToInt(hibyteOffset, lowbyteOffset, false);
            // account for the 4 bytes above
            currOffset += 4;
            if(nextOffset != 0) {
                blocketteBytes = new byte[nextOffset - currOffset];
            } else if(header.getDataOffset() > currOffset) {
                blocketteBytes = new byte[header.getDataOffset() - currOffset];
            } else {
                blocketteBytes = new byte[0];
            }
            inStream.readFully(blocketteBytes);
            if(nextOffset != 0) {
                currOffset = nextOffset;
            } else {
                currOffset += blocketteBytes.length;
            }
            // fix so blockette has full bytes
            byte[] fullBlocketteBytes = new byte[blocketteBytes.length + 4];
            System.arraycopy(blocketteBytes,
                             0,
                             fullBlocketteBytes,
                             4,
                             blocketteBytes.length);
            fullBlocketteBytes[0] = hibyteType;
            fullBlocketteBytes[1] = lowbyteType;
            fullBlocketteBytes[2] = hibyteOffset;
            fullBlocketteBytes[3] = lowbyteOffset;
            Blockette b = Blockette.parseBlockette(type, fullBlocketteBytes);
            dataRec.addBlockette(b);
            if(nextOffset == 0) {
                break;
            }
        }
        Blockette[] allBs = dataRec.getBlockettes(1000);
        if(allBs.length == 0) {
            // no data
            throw new SeedFormatException("no blockette 1000");
        } else if(allBs.length > 1) {
            throw new SeedFormatException("Multiple blockette 1000s in the volume. "
                    + allBs.length);
        }
        // System.out.println("allBs.length="+allBs.length);
        Blockette1000 b1000 = (Blockette1000)allBs[0];
        // System.out.println(b1000);
        byte[] timeseries;
        if(header.getDataOffset() == 0) {
            // data record with no data, so gobble up the rest of the record
            timeseries = new byte[b1000.getDataRecordLength() - currOffset];
        } else {
            timeseries = new byte[b1000.getDataRecordLength()
                    - header.getDataOffset()];
        }
        // System.out.println("getDataRecordLength() = "+
        // b1000.getDataRecordLength());
        inStream.readFully(timeseries);
        dataRec.setData(timeseries);
        return dataRec;
    }

    public String toString() {
        String s = "Data " + super.toString();
        s += "\n" + data.length + " bytes of data read.";
        return s;
    }

    public void writeASCII(Writer out) throws IOException {
        out.write("DataRecord\n");
        getHeader().writeASCII(out);
        Blockette[] b = getBlockettes();
        for(int i = 0; i < b.length; i++) {
            b[i].writeASCII(out);
        }
        out.write("End DataRecord\n");
    }

    protected byte[] data;

    byte ZERO_BYTE = 0;

    int RECORD_SIZE = 4096;
} // DataRecord
