package edu.sc.seis.seisFile.mseed3;

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
import java.text.DecimalFormat;

import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class MSeed3Record  {


    public MSeed3Record(MSeed3Header header, byte[] data) {
        this(header);
        setData(data);
    }
    
    public MSeed3Record(MSeed3Header header) {
        this.header = header;
    }

    public MSeed3Record(MSeed3Record record) {
        this(new MSeed3Header());
        header.setNetworkCode(record.getHeader().getNetworkCode());
        header.setStationCode(record.getHeader().getStationCode());
        header.setLocationCode(record.getHeader().getLocationCode());
        header.setChannelCode(record.getHeader().getChannelCode());
        header.setQualityIndicator(record.getHeader().getQualityIndicator());
        header.setDataVersion(record.getHeader().getDataVersion());
        header.setRecordLength(record.getHeader().getRecordLength());
        header.setStartTime(record.getHeader().getStartTime());
        header.setNumSamples(record.getHeader().getNumSamples());
        header.setSampleRate(record.getHeader().getSampleRate());
        header.setDataCRC(record.getHeader().getDataCRC());
        header.setFlags(record.getHeader().getFlags());
        header.setDataEncodingFormat(record.getHeader().getDataEncodingFormat());
        header.setOpaqueHeaders(record.getHeader().getOpaqueHeaders());
        setData(record.getData());
    }

    protected void recheckDataOffset() {
        getHeader().recheckDataOffset(data == null ? 0 : data.length);
    }

    /**
     * returns the data from this data header unparsed, as a byte array in
     * the format from blockette 1000. The return type is byte[], so the caller
     * must decode the data based on its format.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Decompress the data in this record according to the compression type in
     * the header.
     * 
     * @return
     * @throws SeedFormatException if no blockette 1000 present
     * @throws UnsupportedCompressionType
     * @throws CodecException
     */
    public DecompressedData decompress() throws SeedFormatException, UnsupportedCompressionType, CodecException {
        // in case of record with only blockettes, ex detection blockette, which often have compression type
        // set to 0, which messes up the decompresser even though it doesn't matter since there is no data.
        if (getHeader().getNumSamples() == 0) {
            return new DecompressedData(new int[0]);
        }
        Codec codec = new Codec();
        return codec.decompress(getHeader().getDataEncodingFormat(),
                                getData(),
                                getHeader().getNumSamples(),
                                getHeader().isLittleEndian());
    }

    public void setData(byte[] data) {
        this.data = data;
        recheckDataOffset();
    }

    public int getDataSize() {
        return data.length;
    }

    public MSeed3Header getHeader() {
        return header;
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteStream);
            write(dos);
            dos.close();
            return byteStream.toByteArray();
        } catch(IOException e) {
            // shouldn't happen
            throw new RuntimeException("Caught IOException, should not happen.", e);
        }
    }

    public void write(DataOutputStream dos) throws IOException {
        recheckDataOffset();
        getHeader().write(dos);
        dos.write(data);
    }

    public void printData(PrintWriter out) {
        byte[] d = getData();
        DecimalFormat byteFormat = new DecimalFormat("000");
        for (int i = 0; i < d.length; i++) {
            out.write(byteFormat.format(0xff & d[i]) + " ");
            if (i % 4 == 3) {out.write("  ");}
            if (i % 16 == 15 && i != 0) {
                out.write("\n");
            }
        }
    }

    public static MSeed3Record read(DataInput inStream)
            throws IOException, SeedFormatException {
        try {
            MSeed3Header header = MSeed3Header.read(inStream);
            boolean swapBytes = header.isLittleEndian();
            /*
             * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
             * "Offset to first blockette must be larger than the header size");
             */
            MSeed3Record dataRec = new MSeed3Record(header);
            byte[] timeseries;
            if (header.getDataOffset() == 0) {
                timeseries = new byte[0];
            } else {
                if (header.getRecordLength() < header.getDataOffset()) {
                    throw new SeedFormatException("recordSize < header.getDataOffset(): " + header.getRecordLength() + " < "
                            + header.getDataOffset());
                }
                timeseries = new byte[header.getRecordLength() - header.getDataOffset()];
                inStream.readFully(timeseries);
            }
            dataRec.setData(timeseries);
            return dataRec;
        } catch(SeedFormatException e) {
            throw e;
        }
    }

    public String toString() {
        String s = "MSeed3 " + header.toString();
        s += "\n" + data.length + " bytes of data read.";
        return s;
    }

    protected MSeed3Header header;
    
    protected byte[] data;

    byte ZERO_BYTE = 0;
}
