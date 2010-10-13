package edu.sc.seis.seisFile.seedlink;

import java.io.IOException;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class SeedlinkPacket {

    public SeedlinkPacket(byte[] bytes) {
        if (bytes.length != HEADER_SIZE + DATA_SIZE) {
            throw new IllegalArgumentException("Nunber of bytes must be " + (HEADER_SIZE + DATA_SIZE) + " but given "
                    + bytes.length);
        }
        // seedlink packets start with "SL"
        byte[] headerBytes = new byte[HEADER_SIZE];
        System.arraycopy(bytes, 0, headerBytes, 0, HEADER_SIZE);
        String header = new String(headerBytes);
        if (!header.startsWith(SL_PREFIX)) {
            throw new IllegalArgumentException("SeedLink packets must start with 'SL', not '"
                    + header.substring(0, SL_PREFIX.length()));
        }
        seqNum = header.substring(SL_PREFIX.length());
        mseed = new byte[DATA_SIZE];
        System.arraycopy(bytes, HEADER_SIZE, mseed, 0, DATA_SIZE);
    }

    public DataRecord getMiniSeed() throws IOException, SeedFormatException {
        return (DataRecord)SeedRecord.read(mseed); // seed link only uses data records
    }

    public byte[] getMseedBytes() {
        return mseed;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public boolean isInfoPacket() {
        return seqNum.startsWith("INFO ");
    }

    public boolean isInfoTerminationPacket() {
        return seqNum.equals("INFO  ");
    }

    public boolean isInfoContinuesPacket() {
        return seqNum.equals("INFO *");
    }

    private byte[] mseed;

    private String seqNum;

    public static final int HEADER_SIZE = 8;

    public static final int DATA_SIZE = 512;

    public static final String SL_PREFIX = "SL";

    public static final int PACKET_SIZE = HEADER_SIZE + DATA_SIZE;
}
