package edu.sc.seis.seisFile.mseed3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.Utility;

public class MSeed3Header {

    protected String recordIndicator = DEFAULT_RECORD_INDICATOR;

    protected byte miniSeedVersion = DEFAULT_MINISEED_VERSION;

    protected String networkCode;

    protected String stationCode;

    protected String locationCode;

    protected String channelCode;

    protected char qualityIndicator;

    protected byte dataVersion;

    protected int recordLength;

    protected long startTime;

    protected int numSamples;

    protected float sampleRate;

    protected int dataCRC;

    protected int dataOffset = FIXED_HEADER_SIZE;

    protected byte flags = 1; // go ahead and say big endian

    protected byte sampleEncodingFormat;

    protected byte numOpaqueHeaders;

    protected String[] opaqueHeaders = new String[0];

    /**
     * creates a DataHeader object with nothing set (ie nulls and zeros)
     */
    public MSeed3Header() {}

    /**
     * Writes an ASCII version of the record header. This is not meant to be a
     * definitive ascii representation, merely to give something to print for
     * debugging purposes. Ideally each field of the header should be printed in
     * the order is appears in the header in a visually appealing way.
     * 
     * @param out
     *            a Writer
     * 
     */
    public void writeASCII(PrintWriter out) throws IOException {
        writeASCII(out, "");
    }

    public void writeASCII(PrintWriter out, String indent) throws IOException {
        out.println(indent + getNetworkCode().trim() + "." + getStationCode().trim() + "." + getChannelCode());
        out.println(indent + " start = " + getStartTimeString());
        out.println(indent + " numPTS = " + getNumSamples());
        out.println();
        out.println(indent + " RecordIndicator ('MS')= " + getRecordIndicator());
        out.println(indent + " miniSEEDversion (3)= " + getMiniSeedVersion());
        out.println(indent + " Networkcode = " + getNetworkCode().trim());
        out.println(indent + " Stationcode = " + getStationCode().trim());
        out.println(indent + " Locationcodes = " + getLocationCode().trim());
        out.println(indent + " Channelcodes = " + getChannelCode().trim());
        out.println(indent + " QualityIndicator = " + getQualityIndicator());
        out.println(indent + " DataVersionindicator = " + getDataVersion());
        out.println(indent + " Recordlength = " + getRecordLength());
        out.println(indent + " Recordstarttime = " + getStartTime());
        out.println(indent + " Numberofsamples = " + getNumSamples());
        out.println(indent + " Samplerate = " + getSampleRate());
        out.println(indent + " CRC­32ofdata = " + getDataCRC());
        out.println(indent + " Offsettodata = " + getDataOffset());
        out.println(indent + " Flags = " + getFlags());
        out.println(indent + " sampleEncodingFormat = " + getDataEncodingFormat());
        out.println(indent + " Numberofopaqueheadersthatfollow = " + getNumOpaqueHeaders());
        for (int i = 0; i < getNumOpaqueHeaders(); i++) {
            out.println(indent + "  " + getOpaqueHeader(i));
        }
    }

    public String getOpaqueHeader(int i) {
        return opaqueHeaders[i];
    }

    /**
     * Instantiate an object of this class and read an FSDH byte stream into it,
     * parsing the contents into the instance variables of this object, which
     * represent the individual FSDH fields.<br>
     * 
     * @param in
     *            miniSEED3 data stream .
     * 
     * @return an object of this class with fields filled from 'in' parameter
     * 
     * @throws IOException
     * @throws SeedFormatException
     */
    public static MSeed3Header read(DataInput in) throws IOException, SeedFormatException {
        byte[] buf = new byte[FIXED_HEADER_SIZE];
        in.readFully(buf);
        MSeed3Header header = new MSeed3Header();
        header.read(buf, 0);
        int endOpaque = header.getDataOffset();
        if (header.getDataOffset() == 0) {
            endOpaque = header.getRecordLength();
        }
        byte[] opaqueTemp = new byte[endOpaque - header.getFixedSize()];
        in.readFully(opaqueTemp);
        String opaque = new String(opaqueTemp);
        if (opaque.contains(END_OPAQUE)) {
            String[] opaqueArray = opaque.split(END_OPAQUE);
            if (opaqueArray.length != header.getNumOpaqueHeaders()) {
                throw new SeedFormatException("Num opaque headers doesn't match number of opaque headers: expected "
                        + header.getNumOpaqueHeaders() + " found: " + opaqueArray.length);
            }
            for (int i = 0; i < opaqueArray.length; i++) {
                // trim the '~'
                opaqueArray[i] = opaqueArray[i].substring(0, opaqueArray[i].length() - 1);
            }
            header.setOpaqueHeaders(opaqueArray);
        }
        return header;
    }

    /**
     * populates this object with Fixed Section Data Header info.
     * 
     * @param buf
     *            data buffer containing FSDH information
     * @param offset
     *            byte offset to begin reading buf
     */
    protected void read(byte[] buf, int offset) {
        // flags needed early
        flags = buf[offset + FLAGS_OFFSET];
        boolean byteSwapFlag = isLittleEndian();
        recordIndicator = new String(buf, offset, 2);
        offset += 2;
        miniSeedVersion = buf[offset];
        offset++;
        networkCode = new String(buf, offset, NETWORK_CODE_LENGTH).trim();
        offset += NETWORK_CODE_LENGTH;
        stationCode = new String(buf, offset, STATION_CODE_LENGTH).trim();
        offset += STATION_CODE_LENGTH;
        locationCode = new String(buf, offset, LOCATION_CODE_LENGTH).trim();
        offset += LOCATION_CODE_LENGTH;
        channelCode = new String(buf, offset, CHANNEL_CODE_LENGTH).trim();
        offset += CHANNEL_CODE_LENGTH;
        qualityIndicator = (char)buf[offset];
        offset++;
        dataVersion = buf[offset];
        offset++;
        recordLength = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], byteSwapFlag);
        offset += 4;
        startTime = Utility.bytesToLong(buf[offset],
                                        buf[offset + 1],
                                        buf[offset + 2],
                                        buf[offset + 3],
                                        buf[offset + 4],
                                        buf[offset + 5],
                                        buf[offset + 6],
                                        buf[offset + 7],
                                        byteSwapFlag);
        offset += 8;
        numSamples = Utility.bytesToInt(buf[offset], // careful if negative!!!
                                        buf[offset + 1],
                                        buf[offset + 2],
                                        buf[offset + 3],
                                        byteSwapFlag);
        offset += 4;
        sampleRate = Utility.bytesToFloat(buf, offset, byteSwapFlag);
        offset += 4;
        dataCRC = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], byteSwapFlag);
        offset += 4;
        dataOffset = Utility.uBytesToInt(buf[offset], buf[offset + 1], byteSwapFlag);
        offset += 2;
        // flags
        offset++;
        sampleEncodingFormat = buf[offset];
        offset++;
        numOpaqueHeaders = buf[offset];
        offset++;
    }

    /**
     * write DataHeader contents to a DataOutput stream
     * 
     * @param dos
     *            DataOutput stream to write to
     */
    protected void write(DataOutput dos) throws IOException {
        dos.write(recordIndicator.getBytes("ASCII"));
        dos.write(miniSeedVersion);
        dos.write(Utility.pad(getNetworkCode().getBytes("ASCII"), NETWORK_CODE_LENGTH, (byte)32));
        dos.write(Utility.pad(getStationCode().getBytes("ASCII"), STATION_CODE_LENGTH, (byte)32));
        dos.write(Utility.pad(getLocationCode().getBytes("ASCII"), LOCATION_CODE_LENGTH, (byte)32));
        dos.write(Utility.pad(getChannelCode().getBytes("ASCII"), CHANNEL_CODE_LENGTH, (byte)32));
        dos.write(qualityIndicator);
        dos.write(dataVersion);
        dos.writeInt(getRecordLength());
        dos.writeLong(getStartTime());
        dos.writeInt(getNumSamples());
        dos.writeFloat(getSampleRate());
        dos.writeInt(getDataCRC());
        dos.writeShort((short)getDataOffset());
        dos.write(flags);
        dos.write(sampleEncodingFormat);
        dos.write(numOpaqueHeaders);
        for (int i = 0; i < opaqueHeaders.length; i++) {
            dos.write((opaqueHeaders[i] + END_OPAQUE).getBytes("ASCII"));
        }
    }

    protected void recheckDataOffset(int dataLength) {
        int size = getSizeWithOpaque();
        dataOffset = size;
        size += dataLength;
        if (size != getRecordLength()) {
            setRecordLength(size);
        }
        if (dataLength == 0) {
            setDataEncodingFormat((byte)0);
        }
    }

    public short getFixedSize() {
        return FIXED_HEADER_SIZE;
    }

    public int getSizeWithOpaque() {
        int out = getFixedSize();
        for (int i = 0; i < opaqueHeaders.length; i++) {
            out += opaqueHeaders[i].length() + 1;
        }
        return out;
    }

    public long[] getTimeRange() {
        return new long[] {getStartTime(), getLastSampleTime()};
    }

    /**
     * return a long containing the derived last sample time for this record
     * adjusted for leap seconds.
     */
    public long getLastSampleTime() {
        return getStartTime() + getSamplePeriodAsMicros() * (getNumSamples() - 1)
                - (isPosLeapSecInRecord() ? SEC_MICROS : 0);
    }

    /** Start time as a Date. This does NOT adjust for possible leap secs. */
    public Date getStartTimeDate() {
        return new Date(getStartTime() / 1000);
    }

    /**
     * Get the value of startTime.
     * 
     * @return Value of startTime.
     */
    public String getStartTimeString() {
        return longToDateString(getStartTime(), isStartTimeInLeapSecond());
    }

    /**
     * get the value of end time. derived from Start time, sample rate, and
     * number of samples.
     * 
     * @return the value of end time
     */
    public String getLastSampleTimeString() {
        long time = getLastSampleTime();
        boolean isLeapSec = false;
        if (isEndTimeInLeapSecond()) {
            long modDay = time % (DAY_MICROS);
            if (modDay > LAST_SEC_MICROS) {
                isLeapSec = true;
                System.out.println("ModDay "+modDay);
            }
        }
        System.out.println("LongTodateString "+time+"  "+isLeapSec);
        return longToDateString(time, isLeapSec);
    }

    /*
     * public String getPredictedNextStartTimeString() { long time =
     * getPredictedNextStartTime(); boolean isLeapSec = false; if
     * (isPosLeapSecInRecord() && time % 86400 == 1) { isLeapSec = true; }
     * return longToDateString(time, isLeapSec); }
     */
    public long getSamplePeriodAsMicros() {
        if (getSampleRate() < 0) {
            // period in seconds
            return Math.round(-1 * ((double)getSampleRate()) * SEC_MICROS);
        } else {
            // rate in hertz
            return Math.round(SEC_MICROS / ((double)getSampleRate()));
        }
    }

    public boolean isLittleEndian() {
        boolean byteSwapFlag = (flags & 0x1) == 0x0; // bit 0 ==1 means big
                                                     // endian, ==0 means little
                                                     // endian
        return byteSwapFlag;
    }

    public boolean isStartTimeInLeapSecond() {
        return (getFlags() & 2) == 2;
    }
    
    public boolean isEndTimeInLeapSecond() {
        long durMicros = getSamplePeriodAsMicros() * (getNumSamples() - 1 );
        return (isPosLeapSecInRecord() && ( (getStartTime() + durMicros) % DAY_MICROS < SEC_MICROS ))
                || (isStartTimeInLeapSecond() && (durMicros < SEC_MICROS));
    }

    public void setStartTimeInLeapSecond(boolean b) {
        if (b) {
            flags = (byte)(flags | 2);
        } else {
            flags = (byte)(flags & (255 - 2));
        }
    }

    public boolean isPosLeapSecInRecord() {
        return (getFlags() & 4) == 4;
    }

    public void setPosLeapSecInRecord(boolean b) {
        if (b) {
            flags = (byte)(flags | 4);
        } else {
            flags = (byte)(flags & (255 - 4));
        }
    }

    public boolean isNegLeapSecInRecord() {
        return (getFlags() & 8) == 8;
    }

    public void setNegLeapSecInRecord(boolean b) {
        if (b) {
            flags = (byte)(flags | 8);
        } else {
            flags = (byte)(flags & (255 - 8));
        }
    }

    public boolean isTimeTagQuestionable() {
        return (getFlags() & 16) == 16;
    }

    public void setTimeTagQuestionable(boolean b) {
        if (b) {
            flags = (byte)(flags | 16);
        } else {
            flags = (byte)(flags & (255 - 16));
        }
    }

    public boolean isClockLocked() {
        return (getFlags() & 32) == 32;
    }

    public void setClockLocked(boolean b) {
        if (b) {
            flags = (byte)(flags | 32);
        } else {
            flags = (byte)(flags & (255 - 32));
        }
    }

    /**
     * Present a default string representation of the contents of this object
     * 
     * @return formatted string of object contents
     */
    public String toString() {
        String s = super.toString() + " ";
        s += " " + getNetworkCode() + "." + getStationCode() + "." + getLocationCode() + "." + getChannelCode() + "."
                + getStartTimeString() + "  " + getSampleRate() * getNumSamples() + " " + " " + getDataOffset();
        return s;
    }

    public String getRecordIndicator() {
        return recordIndicator;
    }

    public void setRecordIndicator(String recordIndicator) {
        this.recordIndicator = recordIndicator;
    }

    public byte getMiniSeedVersion() {
        return miniSeedVersion;
    }

    public void setMiniSeedVersion(byte miniSeedVersion) {
        this.miniSeedVersion = miniSeedVersion;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public char getQualityIndicator() {
        return qualityIndicator;
    }

    public void setQualityIndicator(char qualityIndicator) {
        this.qualityIndicator = qualityIndicator;
    }

    public byte getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(byte dataVersion) {
        this.dataVersion = dataVersion;
    }

    public int getRecordLength() {
        return recordLength;
    }

    public void setRecordLength(int recordLength) {
        this.recordLength = recordLength;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getDataCRC() {
        return dataCRC;
    }

    public void setDataCRC(int dataCRC) {
        this.dataCRC = dataCRC;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public byte getDataEncodingFormat() {
        return sampleEncodingFormat;
    }

    public void setDataEncodingFormat(byte dataEncodingFormat) {
        this.sampleEncodingFormat = dataEncodingFormat;
    }

    public byte getNumOpaqueHeaders() {
        return numOpaqueHeaders;
    }

    public void setNumOpaqueHeaders(byte numOpaqueHeaders) {
        this.numOpaqueHeaders = numOpaqueHeaders;
    }

    public String[] getOpaqueHeaders() {
        return opaqueHeaders;
    }

    public void setOpaqueHeaders(String[] opaqueHeaders) {
        if (opaqueHeaders != null) {
            this.opaqueHeaders = opaqueHeaders;
        } else {
            this.opaqueHeaders = new String[0];
        }
        numOpaqueHeaders = (byte)opaqueHeaders.length;
        dataOffset = getSizeWithOpaque();
    }

    public int getDataOffset() {
        return dataOffset;
    }

    protected static String longToDateString(long longMicros, boolean isLeapSec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");
        sdf.setTimeZone(TZ_UTC);
        // micros to millis and back up 1 sec if a leap sec to keep min correct,
        // will make 59 60 in string
        Date d = new Date(longMicros / 1000 );
        String out = sdf.format(d);
        int micros = (int)(longMicros % 1000);
        if (micros < 10) {
            out += "00" + micros;
        } else if (micros < 100) {
            out += "0" + micros;
        } else {
            out += micros;
        }
        if (isLeapSec) {
            out = out.substring(0, 13) + "60" + out.substring(15);
        }
        return out;
    }

    public static final TimeZone TZ_UTC = TimeZone.getTimeZone("UTC");

    public static final int NETWORK_CODE_LENGTH = 2;

    public static final int STATION_CODE_LENGTH = 5;

    public static final int LOCATION_CODE_LENGTH = 2;

    public static final int CHANNEL_CODE_LENGTH = 3;

    public static final int FIXED_HEADER_SIZE = 34 + NETWORK_CODE_LENGTH + STATION_CODE_LENGTH + LOCATION_CODE_LENGTH
            + CHANNEL_CODE_LENGTH;

    public static final int FLAGS_OFFSET = 43;

    protected static final String DEFAULT_RECORD_INDICATOR = "MS";

    protected static final byte DEFAULT_MINISEED_VERSION = (byte)3;

    public static final String END_OPAQUE = "~";
    
    public static final long SEC_MICROS = 1000000l;
    
    public static final long DAY_MICROS = 86400 * SEC_MICROS;
    
    public static final long LAST_SEC_MICROS = DAY_MICROS - SEC_MICROS;
    
}
