package edu.sc.seis.seisFile.seedlink;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import edu.sc.seis.seisFile.mseed3.MSeed3Record;

/**
 * A SeedLink packet. Version 3 packets are a fixed 8 byte header and 512 bytes of miniSEED 2. Version 4
 * packets have a variable length payload in one of several formats, see
 * <a href="https://docs.fdsn.org/projects/seedlink/en/latest/protocol.html">the protocol</a>.
 */
public class SeedlinkPacket {

    public SeedlinkPacket(byte[] bytes) throws SeedlinkException {
        if (bytes.length != HEADER_SIZE + DATA_SIZE) {
            throw new SeedlinkException("Nunber of bytes must be " + (HEADER_SIZE + DATA_SIZE) + " but given "
                    + bytes.length);
        }
        // seedlink packets start with "SL"
        byte[] headerBytes = new byte[HEADER_SIZE];
        System.arraycopy(bytes, 0, headerBytes, 0, HEADER_SIZE);
        String header = new String(headerBytes);
        if (!header.startsWith(SL_PREFIX)) {
            throw new SeedlinkException("SeedLink packets must start with 'SL', not '"
                    + header.substring(0, SL_PREFIX.length()));
        }
        seqNum = header.substring(SL_PREFIX.length());
        mseed = new byte[DATA_SIZE];
        System.arraycopy(bytes, HEADER_SIZE, mseed, 0, DATA_SIZE);
        protocolVersion = 3;
        format = FORMAT_MSEED2;
        subformat = UNKNOWN_SUBFORMAT;
    }

    /**
     * Creates a version 4 packet.
     * @param format the payload format, '2' for miniSEED 2, '3' for miniSEED 3 or 'J' for JSON.
     * @param subformat the payload subformat.
     * @param seq the sequence number.
     * @param stationId the station id, NET_STA.
     * @param payload the payload bytes.
     */
    public SeedlinkPacket(char format, char subformat, long seq, String stationId, byte[] payload) {
        this.protocolVersion = 4;
        this.format = format;
        this.subformat = subformat;
        this.seqNum = Long.toUnsignedString(seq);
        this.stationId = stationId;
        this.mseed = payload;
    }

    /**
     * Reads one version 4 packet, header and payload, from the stream.
     * @param in the stream to read from.
     * @return the packet.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if the header is not a valid version 4 header.
     */
    public static SeedlinkPacket readV4(DataInputStream in) throws IOException, SeedlinkException {
        byte[] fixed = new byte[V4_HEADER_FIXED_SIZE];
        in.readFully(fixed);
        if (fixed[0] != 'S' || fixed[1] != 'E') {
            throw new SeedlinkException("SeedLink v4 packets must start with '" + V4_PREFIX + "', not '"
                    + new String(fixed, 0, 2, StandardCharsets.US_ASCII) + "'");
        }
        ByteBuffer header = ByteBuffer.wrap(fixed).order(ByteOrder.LITTLE_ENDIAN);
        char format = (char)(fixed[2] & 0xFF);
        char subformat = (char)(fixed[3] & 0xFF);
        long payloadLength = header.getInt(4) & 0xFFFFFFFFL;
        long seq = header.getLong(8);
        int stationIdLength = fixed[16] & 0xFF;
        if (payloadLength > MAX_V4_PAYLOAD) {
            throw new SeedlinkException("SeedLink v4 payload length too large: " + payloadLength);
        }
        byte[] stationBytes = new byte[stationIdLength];
        in.readFully(stationBytes);
        byte[] payload = new byte[(int)payloadLength];
        in.readFully(payload);
        return new SeedlinkPacket(format,
                                  subformat,
                                  seq,
                                  new String(stationBytes, StandardCharsets.US_ASCII),
                                  payload);
    }

    public DataRecord getMiniSeed() throws IOException, SeedFormatException {
        if (!isMiniSeed()) {
            throw new SeedFormatException("SeedLink packet payload is not miniSEED 2, format=" + format + subformat);
        }
        return (DataRecord)SeedRecord.read(mseed); // seed link only uses data records
    }

    /**
     * Gets the miniSEED 3 payload of a version 4 packet.
     * @return the miniSEED 3 record.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedFormatException if the payload is not miniSEED 3.
     */
    public MSeed3Record getMiniSeed3() throws IOException, SeedFormatException {
        if (!isMiniSeed3()) {
            throw new SeedFormatException("SeedLink packet payload is not miniSEED 3, format=" + format + subformat);
        }
        return MSeed3Record.fromByteBuffer(ByteBuffer.wrap(mseed).order(ByteOrder.LITTLE_ENDIAN));
    }

    /** The payload, miniSEED 2 for version 3 and whatever the format is for version 4. */
    public byte[] getMseedBytes() {
        return mseed;
    }

    /** Hex for version 3, decimal for version 4. */
    public String getSeqNum() {
        return seqNum;
    }

    /**
     * Gets the sequence number as a number.
     * @return the sequence number, or -1 for a version 3 INFO packet that has none.
     */
    public long getSeqNumLong() {
        if (isV4()) {
            return Long.parseUnsignedLong(seqNum);
        }
        if (isInfoPacket()) {
            return -1;
        }
        return Long.parseLong(seqNum.trim(), 16);
    }

    public boolean isV4() {
        return protocolVersion == 4;
    }

    public char getFormat() {
        return format;
    }

    /** The subformat, '\0' (unknown) for version 3. */
    public char getSubformat() {
        return subformat;
    }

    /** The station id, NET_STA, or null for version 3 which has no station in the header. */
    public String getStationId() {
        return stationId;
    }

    public boolean isMiniSeed() {
        return format == FORMAT_MSEED2;
    }

    public boolean isMiniSeed3() {
        return format == FORMAT_MSEED3;
    }

    public boolean isInfoPacket() {
        if (isV4()) {
            return format == FORMAT_JSON;
        }
        return seqNum.startsWith("INFO ");
    }

    public boolean isInfoTerminationPacket() {
        if (isV4()) {
            return isInfoPacket();
        }
        return seqNum.equals("INFO  ");
    }

    public boolean isInfoContinuesPacket() {
        if (isV4()) {
            return false;
        }
        return seqNum.equals("INFO *");
    }

    /** True for a version 4 error packet, the JSON format with the error subformat. */
    public boolean isErrorPacket() {
        return isV4() && format == FORMAT_JSON && subformat == SUBFORMAT_JSON_ERROR;
    }

    private byte[] mseed;

    private String seqNum;

    private int protocolVersion;

    private char format;

    private char subformat;

    private String stationId;

    public static final int HEADER_SIZE = 8;

    public static final int DATA_SIZE = 512;

    public static final String SL_PREFIX = "SL";

    public static final int PACKET_SIZE = HEADER_SIZE + DATA_SIZE;

    public static final String V4_PREFIX = "SE";

    /** Signature, format, subformat, payload length, sequence and station id length. */
    public static final int V4_HEADER_FIXED_SIZE = 17;

    /** Sanity limit to avoid allocating huge buffers on a corrupt stream. */
    public static final long MAX_V4_PAYLOAD = 16 * 1024 * 1024;

    public static final char FORMAT_MSEED2 = '2';

    public static final char FORMAT_MSEED3 = '3';

    public static final char FORMAT_JSON = 'J';

    public static final char SUBFORMAT_JSON_ERROR = 'E';

    public static final char UNKNOWN_SUBFORMAT = '\0';
}
