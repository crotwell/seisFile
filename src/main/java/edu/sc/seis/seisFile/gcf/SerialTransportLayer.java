package edu.sc.seis.seisFile.gcf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerialTransportLayer {

    public SerialTransportLayer(int seqNum, AbstractGCFBlock payload, boolean isSerial) throws GCFFormatException {
        this(new SerialTransportHeader(seqNum, payload.getSize()), payload);
        if (isSerial && payload instanceof GCFBlock) {
            GCFBlock b = (GCFBlock)payload;
            if (b.getHeader().getCompression() == 1 && ! b.isSerial) {
                throw new GCFFormatException("Can't write non-serial GCF block to serial");
            }
        }
    }

    public SerialTransportLayer(SerialTransportHeader header, AbstractGCFBlock payload) {
        this(header, payload, 0, 0);
    }

    public SerialTransportLayer(SerialTransportHeader header, AbstractGCFBlock payload, int checksum, int streamIdLSB) {
        super();
        this.header = header;
        this.payload = payload;
        this.checksum = checksum;
        this.streamIdLSB = streamIdLSB;
    }

    public void write(DataOutputStream out) throws IOException {
        SerialCheckSumOutputStream csout = new SerialCheckSumOutputStream(out);
        header.write(out);
        payload.write(out);
        checksum = csout.writeCheckSum();
    }

    public SerialTransportHeader getHeader() {
        return header;
    }

    public int getChecksum() {
        return checksum;
    }

    public AbstractGCFBlock getPayload() {
        return payload;
    }

    
    public int getStreamIdLSB() {
        return streamIdLSB;
    }

    public static SerialTransportLayer read(DataInput in) throws GCFFormatException, IOException {
        byte[] headerBytes = new byte[4];
        in.readFully(headerBytes);
        SerialTransportHeader header = SerialTransportHeader.fromBytes(headerBytes, 0);
        byte[] transportData = new byte[header.getBlockSize()];
        in.readFully(transportData);
        int checkSum = (in.readByte() << 8) + in.readByte();
        DataInputStream gcfIn = new DataInputStream(new ByteArrayInputStream(transportData));
        AbstractGCFBlock gcf = AbstractGCFBlock.read(gcfIn, true);
        byte streamIdLSB = transportData[11];
        return new SerialTransportLayer(header, gcf, checkSum, streamIdLSB);
    }

    SerialTransportHeader header;

    int checksum;

    AbstractGCFBlock payload;
    
    int streamIdLSB;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + checksum;
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SerialTransportLayer other = (SerialTransportLayer)obj;
        if (checksum != other.checksum)
            return false;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        if (payload == null) {
            if (other.payload != null)
                return false;
        } else if (!payload.equals(other.payload))
            return false;
        return true;
    }
}
