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
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;

public class DataRecord extends SeedRecord implements Serializable {

    public DataRecord(DataHeader header) {
        super(header);
    }

    public DataRecord(DataRecord record) {
        super(new DataHeader(record.getHeader().getSequenceNum(),
                             record.getHeader().getTypeCode(),
                             record.getHeader().isContinuation()));
        RECORD_SIZE = record.RECORD_SIZE;
        getHeader().setActivityFlags(record.getHeader().getActivityFlags());
        getHeader().setChannelIdentifier(record.getHeader()
                .getChannelIdentifier());
        getHeader().setDataBlocketteOffset((short)record.getHeader()
                .getDataBlocketteOffset());
        getHeader().setDataOffset((short)record.getHeader().getDataOffset());
        getHeader().setDataQualityFlags(record.getHeader()
                .getDataQualityFlags());
        getHeader().setIOClockFlags(record.getHeader().getIOClockFlags());
        getHeader().setLocationIdentifier(record.getHeader()
                .getLocationIdentifier());
        getHeader().setNetworkCode(record.getHeader().getNetworkCode());
        getHeader().setNumSamples((short)record.getHeader().getNumSamples());
        getHeader().setSampleRateFactor((short)record.getHeader()
                .getSampleRateFactor());
        getHeader().setSampleRateMultiplier((short)record.getHeader()
                .getSampleRateMultiplier());
        getHeader().setStartBtime(record.getHeader().getStartBtime());
        getHeader().setStationIdentifier(record.getHeader()
                .getStationIdentifier());
        getHeader().setTimeCorrection(record.getHeader().getTimeCorrection());
        try {
            setData(record.getData());
            for(int j = 0; j < record.getBlockettes().length; j++) {
                blockettes.add(record.getBlockettes()[j]);
            }
        } catch(SeedFormatException e) {
            throw new RuntimeException("Shouldn't happen as record was valid and we are copying it");
        }
    }

    public void addBlockette(Blockette b) throws SeedFormatException {
        if(b == null) {
            throw new IllegalArgumentException("Blockette cannot be null");
        }
        if (b instanceof BlocketteUnknown) {
            b = new DataBlocketteUnknown(((BlocketteUnknown)b).info, b.getType(), ((BlocketteUnknown)b).getSwapBytes());
        }
        if(b instanceof DataBlockette) {
            super.addBlockette(b);
            getHeader().setNumBlockettes((byte)(getHeader().getNumBlockettes() + 1));
        } else {
            throw new SeedFormatException("Cannot add non-data blockettes to a DataRecord "
                    + b.getType());
        }
        if (b instanceof Blockette1000) {
            RECORD_SIZE = ((Blockette1000)b).getLogicalRecordLength();
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
    
    public byte[] toByteArray() {
        try {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteStream);
        write(dos);
        dos.close();
        return byteStream.toByteArray();
        } catch (IOException e) {
            // shouldn't happen
            throw new RuntimeException("Caught IOException, should not happen.", e);
        }
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

    public void writeData(PrintWriter out) {
        byte[] d = getData();
        for (int i = 0; i < d.length; i++) {
            out.write(byteFormat.format(0xff & d[i])+" ");
            if (i % 4 == 3) {out.write("  ");}
            if (i % 16 == 15 && i != 0) {
                out.write("\n");
            }
        }
    }

    public static SeedRecord readDataRecord(DataInput inStream,
                                               DataHeader header,
                                               int defaultRecordSize)
            throws IOException, SeedFormatException {
        try {
        boolean swapBytes = header.flagByteSwap();
        /*
         * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
         * "Offset to first blockette must be larger than the header size");
         */
        DataRecord dataRec = new DataRecord(header);
        // read garbage between header and blockettes
        if(header.getDataBlocketteOffset() != 0) {
            byte[] garbage = new byte[header.getDataBlocketteOffset()
                    - header.getSize()];
            if(garbage.length != 0) {
                inStream.readFully(garbage);
            }
        }
        byte[] blocketteBytes;
        int currOffset = header.getDataBlocketteOffset();
        if(header.getDataBlocketteOffset() == 0) {
            currOffset = header.getSize();
        }
        int type, nextOffset;
        for(int i = 0; i < header.getNumBlockettes(); i++) {
            // get blockette type (first 2 bytes)
            byte hibyteType = inStream.readByte();
            byte lowbyteType = inStream.readByte();
            type = Utility.uBytesToInt(hibyteType, lowbyteType, swapBytes);
            byte hibyteOffset = inStream.readByte();
            byte lowbyteOffset = inStream.readByte();
            nextOffset = Utility.uBytesToInt(hibyteOffset,
                                             lowbyteOffset,
                                             swapBytes);
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
            Blockette b = SeedRecord.getBlocketteFactory().parseBlockette(type,
                                                   fullBlocketteBytes,
                                                   swapBytes);
            dataRec.blockettes.add(b);
            if(nextOffset == 0) {
                break;
            }
        }
        int recordSize = defaultRecordSize;
        try {
            recordSize = ((Blockette1000)dataRec.getUniqueBlockette(1000)).getDataRecordLength();
        } catch(MissingBlockette1000 e) {
            if(defaultRecordSize == 0) {
                // no default
                throw e;
            }
            // otherwise use default
            recordSize = defaultRecordSize;
        }
        dataRec.RECORD_SIZE = recordSize;
        // read garbage between blockettes and data
        if(header.getDataOffset() != 0) {
            byte[] garbage = new byte[header.getDataOffset() - currOffset];
            if(garbage.length != 0) {
                inStream.readFully(garbage);
            }
        }
        byte[] timeseries;
        if(header.getDataOffset() == 0) {
            // data record with no data, so gobble up the rest of the record
            timeseries = new byte[recordSize - currOffset];
        } else {
            if (recordSize < header.getDataOffset()) {
                throw new SeedFormatException("recordSize < header.getDataOffset(): "+recordSize+" < "+header.getDataOffset());
            }
            timeseries = new byte[recordSize - header.getDataOffset()];
        }
        inStream.readFully(timeseries);
        dataRec.setData(timeseries);
        return dataRec;
        } catch (SeedFormatException e) {
            e.setHeader(header);
            throw e;
        }
    }

    public void setRecordSize(int recordSize) throws SeedFormatException {
        int tmp = RECORD_SIZE;
        RECORD_SIZE = recordSize;
        try {
            recheckDataOffset();
        } catch(SeedFormatException e) {
            RECORD_SIZE = tmp;
            throw e;
        }
    }

    public String toString() {
        String s = "Data " + super.toString();
        s += "\n" + data.length + " bytes of data read.";
        return s;
    }

    protected byte[] data;

    byte ZERO_BYTE = 0;
    
    private static DecimalFormat byteFormat = new DecimalFormat("000");
} // DataRecord
