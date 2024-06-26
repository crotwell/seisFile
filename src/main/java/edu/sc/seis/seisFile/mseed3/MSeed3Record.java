package edu.sc.seis.seisFile.mseed3;

import edu.iris.dmc.seedcodec.*;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.Utility;
import org.json.JSONObject;

import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.zip.CRC32C;

public class MSeed3Record {

    public static final String MINISEED_THREE_MIME = "application/vnd.fdsn.mseed3";

    public static final String TIME_LEAP_SECOND = "TimeLeapSecond";

    /**
     * const for unknown data version, 0
     */
    public static final int UNKNOWN_DATA_VERSION = 0;

    protected String recordIndicator = DEFAULT_RECORD_INDICATOR;

    protected byte formatVersion = DEFAULT_MINISEED_VERSION;

    protected byte flags = 0;

    protected int year = DEFAULT_START_YEAR;

    protected int dayOfYear = DEFAULT_START_DAY_OF_YEAR;

    protected int hour = DEFAULT_START_HOUR;

    protected int minute = DEFAULT_START_MINUTE;

    protected int second = DEFAULT_START_SECOND;

    protected int nanosecond = DEFAULT_START_NANOSECOND;

    protected double sampleRatePeriod = 1;

    protected byte timeseriesEncodingFormat;

    protected byte publicationVersion = UNKNOWN_DATA_VERSION;

    protected int numSamples = 0;

    protected int recordCRC = 0;

    protected byte sourceIdByteLength = 0;

    protected int extraHeadersByteLength = 0;

    protected int dataByteLength = 0;

    protected String sourceIdStr = "";

    protected FDSNSourceId sourceId;

    protected String extraHeadersStr = "";

    protected JSONObject extraHeaders;

    protected byte[] timeseriesBytes = new byte[0];

    /**
     * creates a DataHeader object with nothing set (ie nulls and zeros)
     */
    public MSeed3Record() {
    }

    /**
     * Writes an ASCII version of the record header. This is not meant to be a
     * definitive ascii representation, merely to give something to print for
     * debugging purposes. Ideally each field of the header should be printed in
     * the order is appears in the header in a visually appealing way.
     *
     * @param out a Writer
     */
    public void printASCII(PrintWriter out) throws IOException {
        printASCII(out, "");
    }

    public void printASCII(PrintWriter out, String indent) throws IOException {
        printASCII(out, indent, false);
    }

    public void printASCII(PrintWriter out, String indent, boolean withData) throws IOException {
        out.format(indent + "%s, version %d, %d bytes (format: %d)%n",
                getSourceId(), getPublicationVersion(), getSize(), getFormatVersion());
        out.println(indent + "             start time: " + getStartTimeString());
        out.println(indent + "      number of samples: " + getNumSamples());
        String srateStr = String.format("%.10g", samplingAsRate());
        while (srateStr.endsWith("00")) { srateStr = srateStr.substring(0, srateStr.length()-1);}
        out.format(indent + "       sample rate (Hz): %s%n", srateStr);
        byte b = getFlags();
        out.format(indent + "                  flags: [%d%d%d%d%d%d%d%d] 8 bits%n",
                (b & 0x80) >> 7,
                (b & 0x40) >> 6,
                (b & 0x20) >> 5,
                (b & 0x10) >> 4,
                (b & 0x08) >> 3,
                (b & 0x04) >> 2,
                (b & 0x02) >> 1,
                (b & 0x01)
        );
        if ((b & 0x01) != 0)
            out.println(indent + "                         [Bit 0] Calibration signals present");
        if ((b & 0x02) != 0)
            out.println(indent + "                         [Bit 1] Time tag is questionable");
        if ((b & 0x04) != 0)
            out.println(indent + "                         [Bit 2] Clock locked");
        if ((b & 0x08) != 0)
            out.println(indent + "                         [Bit 3] Undefined bit set");
        if ((b & 0x10) != 0)
            out.println(indent + "                         [Bit 4] Undefined bit set");
        if ((b & 0x20) != 0)
            out.println(indent + "                         [Bit 5] Undefined bit set");
        if ((b & 0x40) != 0)
            out.println(indent + "                         [Bit 6] Undefined bit set");
        if ((b & 0x80) != 0)
            out.println(indent + "                         [Bit 7] Undefined bit set");
        out.format(indent + "                    CRC: 0x%8X%n", getRecordCRC());
        out.format(indent + "    extra header length: %d bytes%n", getExtraHeadersByteLength());
        out.format(indent + "    data payload length: %d bytes%n", getDataByteLength());
        out.format(indent + "       payload encoding: %s (val: %d)%n",
                getTimeseriesEncodingFormatName(), getTimeseriesEncodingFormat());
        if (getExtraHeaders() != null && ! getExtraHeaders().isEmpty()) {
            out.println(indent + "          extra headers:");
            String[] ehLines = getExtraHeadersAsString(2).split("\n");
            for (String line : ehLines) {
                out.println("          " + line);
            }
        }
        if (withData) {
            out.println(indent+"Data:");
            printDataASCII(out, indent);
        }
    }

    public void printDataASCII(PrintWriter out) {
        printDataASCII(out, "");
    }

    public void printDataASCII(PrintWriter out, String indent) {
        try {
            DecompressedData decomp = decompress();
            if (decomp.getType() == DecompressedData.INTEGER) {
                int[] data = decomp.getAsInt();
                printData(out, indent, data);
            } else if (decomp.getType() == DecompressedData.SHORT) {
                short[] data = decomp.getAsShort();
                printData(out, indent, data);
            } else if (decomp.getType() == DecompressedData.FLOAT) {
                float[] data = decomp.getAsFloat();
                printData(out, indent, data);
            } else if (decomp.getType() == DecompressedData.DOUBLE) {
                double[] data = decomp.getAsDouble();
                printData(out, indent, data);
            } else {
                out.println("Unknown data compression: " + decomp.getType());
            }
        } catch (Exception e) {
            out.println("Unable to decompress: " + getTimeseriesEncodingFormat());
            out.println(e);
        }
    }

    public static void printData(PrintWriter out, String indent, int[] data) {
        int cols = 6;
        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (i % cols == 0) {
                out.write(indent);
            }
            out.format("%10d", data[i]);
            if (i % cols == cols - 1 && i != 0) {
                out.write("\n");
            }
        }
        if (i % cols != cols - 1 && i != 0) {
            out.write("\n");
        }
    }

    public static void printData(PrintWriter out, String indent, short[] data) {
        int cols = 6;
        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (i % cols == 0) {
                out.write(indent);
            }
            out.format("%10d ", data[i]);
            if (i % cols == cols - 1 && i != 0) {
                out.write("\n");
            }
        }
        if (i % cols != cols - 1 && i != 0) {
            out.write("\n");
        }
    }

    public static void printData(PrintWriter out, String indent, float[] data) {
        int cols = 6;
        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (i % cols == 0) {
                out.write(indent);
            }
            out.format("%10.8g  ", data[i]);
            if (i % cols == cols - 1 && i != 0) {
                out.write("\n");
            }
        }
        if (i % cols != cols - 1 && i != 0) {
            out.write("\n");
        }
    }

    public static void printData(PrintWriter out, String indent, double[] data) {
        int cols = 6;
        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (i % cols == 0) {
                out.write(indent);
            }
            out.format("%12.10g  ", data[i]);
            if (i % cols == cols - 1 && i != 0) {
                out.write("\n");
            }
        }
        if (i % cols != cols - 1 && i != 0) {
            out.write("\n");
        }
    }

    public void printDataRawBytes(PrintWriter out, String indent) {
        byte[] d = getTimeseriesBytes();
        DecimalFormat byteFormat = new DecimalFormat("000");
        for (int i = 0; i < d.length; i++) {
            if (i % 16 == 0) {
                out.write(indent);
            }
            out.write(byteFormat.format(0xff & d[i]) + " ");
            if (i % 4 == 3) {
                out.write("  ");
            }
            if (i % 16 == 15 && i != 0) {
                out.write("\n");
            }
        }
    }

    /**
     * Instantiate an object of this class and read an FSDH byte stream into it,
     * parsing the contents into the instance variables of this object, which
     * represent the individual FSDH fields.<br>
     *
     * @param in miniSEED3 data stream .
     * @return an object of this class with fields filled from 'in' parameter
     * @throws IOException
     * @throws SeedFormatException
     */
    public static MSeed3Record read(DataInput in) throws IOException, SeedFormatException, FDSNSourceIdException {
        byte[] buf = new byte[FIXED_HEADER_SIZE];
        in.readFully(buf);
        MSeed3Record header = new MSeed3Record();
        header.read(buf, 0);
        buf = new byte[header.sourceIdByteLength];
        in.readFully(buf);
        String sourceIdStr = new String(buf, 0, header.sourceIdByteLength).trim();
        if (sourceIdStr.startsWith(FDSNSourceId.FDSN_PREFIX)) {
            FDSNSourceId sid = FDSNSourceId.parse(sourceIdStr);
            header.sourceId = sid;
        } else {
            header.sourceIdStr = sourceIdStr;
            header.sourceId = null;
        }
        buf = new byte[header.extraHeadersByteLength];
        in.readFully(buf);
        header.extraHeadersStr = new String(buf, 0, header.extraHeadersByteLength).trim();
        if (MAX_DATA_SIZE > 0 && header.dataByteLength > MAX_DATA_SIZE) {
            // maybe careful if dataByteLength is too big???
            throw new RuntimeException("Miniseed3 data too big: "+header.dataByteLength+" > "+MAX_DATA_SIZE);
        }
        header.timeseriesBytes = new byte[header.dataByteLength];
        in.readFully(header.timeseriesBytes);
        return header;
    }

    /**
     * populates this object with Fixed Section Data Header info.
     *
     * @param buf    data buffer containing FSDH information
     * @param offset byte offset to begin reading buf
     */
    protected int read(byte[] buf, int offset) {
        recordIndicator = new String(buf, offset, 2);
        offset += 2;
        formatVersion = buf[offset];
        offset++;
        flags = buf[offset];
        offset++;
        nanosecond = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], true);
        offset += 4;
        year = Utility.bytesToInt(buf[offset], buf[offset + 1], true);
        offset += 2;
        dayOfYear = Utility.bytesToInt(buf[offset], buf[offset + 1], true);
        offset += 2;
        hour = buf[offset];
        offset++;
        minute = buf[offset];
        offset++;
        second = buf[offset];
        offset++;
        timeseriesEncodingFormat = buf[offset];
        offset++;
        setSampleRatePeriod( Utility.bytesToDouble(buf, offset, true));
        offset += 8;
        numSamples = Utility.bytesToInt(buf[offset], // careful if negative!!!
                buf[offset + 1],
                buf[offset + 2],
                buf[offset + 3],
                true);
        offset += 4;
        recordCRC = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], true);
        offset += 4;
        publicationVersion = buf[offset];
        offset++;
        sourceIdByteLength = buf[offset];
        offset++;
        extraHeadersByteLength = Utility.bytesToInt(buf[offset], buf[offset + 1], true);
        offset += 2;
        dataByteLength = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], true);
        offset += 4;
        return offset;
    }

    /**
     * write DataHeader contents to a DataOutput stream
     *
     * @param dos DataOutput stream to write to
     */
    public void write(OutputStream dos) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(recordIndicator.getBytes("ASCII"));
        out.write(formatVersion);
        out.write(flags);
        out.write(Utility.intToLittleEndianByteArray(nanosecond));
        out.write(Utility.shortToLittleEndianByteArray(year));
        out.write(Utility.shortToLittleEndianByteArray(dayOfYear));
        out.write(hour);
        out.write(minute);
        out.write(second);
        out.write(timeseriesEncodingFormat);
        out.write(Utility.doubleToLittleEndianByteArray(sampleRatePeriod));
        out.write(Utility.intToLittleEndianByteArray(numSamples));
        //out.write(Utility.intToLittleEndianByteArray(recordCRC));
        out.write(Utility.intToLittleEndianByteArray(0));
        out.write(publicationVersion);
        byte[] sourceIdBytes = getSourceIdStr().getBytes();
        out.write((byte) (sourceIdBytes.length));
        byte[] extraHeadersBytes = getExtraHeadersAsString(0).getBytes();
        extraHeadersByteLength = extraHeadersBytes.length;
        out.write(Utility.shortToLittleEndianByteArray(extraHeadersBytes.length));
        byte[] tsBytes = getTimeseriesBytes();
        out.write(Utility.intToLittleEndianByteArray(tsBytes.length));
        out.write(sourceIdBytes); // might be wrong if not ascii, should check
        out.write(extraHeadersBytes); // might be wrong if not ascii, should check
        extraHeadersBytes = null; // memory
        out.write(tsBytes);
        tsBytes = null; // memory

        CRC32C crc32c = new CRC32C();
        byte[] outbytes = out.toByteArray();
        crc32c.update(out.toByteArray());

        recordCRC = (int)crc32c.getValue();
        byte[] crcBytes = Utility.intToLittleEndianByteArray(recordCRC);
        outbytes[CRC_OFFSET] = crcBytes[0];
        outbytes[CRC_OFFSET+1] = crcBytes[1];
        outbytes[CRC_OFFSET+2] = crcBytes[2];
        outbytes[CRC_OFFSET+3] = crcBytes[3];
        dos.write(outbytes);
    }

    public static final int CRC_OFFSET = 28;

    // default to 2Gb pre record, which seems crazy big, but...
    public static long MAX_DATA_SIZE = 2*1000*1000*1000;
    static {
        long freeMemory = Runtime.getRuntime().freeMemory();
        if (MAX_DATA_SIZE > freeMemory) {
            MAX_DATA_SIZE = freeMemory/2;
        }
    }

    /**
     * read record contents from a ByteBuffer
     *
     */
    public static MSeed3Record fromByteBuffer(ByteBuffer buf) throws IOException, SeedFormatException {
        if (buf.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new SeedFormatException("Miniseed3 buffer should be little endian, was "+buf.order());
        }
        byte format_m = buf.get();
        byte format_s = buf.get();
        if (buf.get(0) != 77 || buf.get(1) !=  83) {
            throw new SeedFormatException("Miniseed3 record must start with 'MS', but was "+buf.get(0)+" "+buf.get(1));
        }
        if (buf.get(2) !=  3) {
            throw new SeedFormatException("Miniseed3 record version was not 3, was "+buf.get(2));
        }
        MSeed3Record rec = new MSeed3Record();
        rec.formatVersion = buf.get(2);       // 2
        rec.flags = buf.get(3);               // 3
        rec.nanosecond = buf.getInt(4); // 4
        rec.year = buf.getShort(8);
        rec.dayOfYear = buf.getShort(10);
        rec.hour = buf.get(12);
        rec.minute = buf.get(13);
        rec.second = buf.get(14);
        rec.timeseriesEncodingFormat = buf.get(15);
        rec.setSampleRatePeriod( buf.getDouble(16));
        rec.numSamples = buf.getInt(24);
        rec.recordCRC = buf.getInt(28);
        rec.publicationVersion = buf.get(32);
        rec.sourceIdByteLength = buf.get(33);
        rec.extraHeadersByteLength = buf.getShort(34);
        rec.dataByteLength = buf.getInt(36);
        buf.position(40);
        byte[] sidBytes = new byte[rec.sourceIdByteLength];
        buf.get(sidBytes);
        rec.sourceIdStr = new String(sidBytes, StandardCharsets.US_ASCII);
        //rec.sourceId = FDSNSourceId.parse(rec.sourceIdStr);
        byte[] ehBytes = new byte[rec.extraHeadersByteLength];
        buf.position(40+sidBytes.length);
        buf.get(ehBytes);
        rec.extraHeadersStr = new String(ehBytes, StandardCharsets.UTF_8);
        buf.position(40+sidBytes.length+ehBytes.length);
        if (MAX_DATA_SIZE > 0 && rec.dataByteLength > MAX_DATA_SIZE) {
            // maybe careful if dataByteLength is too big???
            throw new RuntimeException("Miniseed3 data too big: "+rec.dataByteLength+" > "+MAX_DATA_SIZE);
        }
        rec.timeseriesBytes = new byte[rec.dataByteLength];
        buf.get(rec.timeseriesBytes);
        return rec;
    }

    /**
     * write DataHeader contents as a ByteBuffer
     *
     */
    public ByteBuffer asByteBuffer() throws IOException {
        byte[] extraHeadersBytes = getExtraHeadersAsString(0).getBytes();
        extraHeadersByteLength = extraHeadersBytes.length;
        byte[] sourceIdBytes = getSourceId().toString().getBytes();
        sourceIdByteLength = (byte) sourceIdBytes.length;
        dataByteLength = timeseriesBytes.length;
        ByteBuffer buf = ByteBuffer.allocate(getSize());
        buf.order( ByteOrder.LITTLE_ENDIAN);
        buf.put(recordIndicator.getBytes("ASCII"));
        buf.put(formatVersion);
        buf.put(flags);
        buf.put(Utility.intToLittleEndianByteArray(nanosecond));
        buf.put(Utility.shortToLittleEndianByteArray(year));
        buf.put(Utility.shortToLittleEndianByteArray(dayOfYear));
        buf.put((byte) hour);
        buf.put((byte) minute);
        buf.put((byte) second);
        buf.put(timeseriesEncodingFormat);
        buf.put(Utility.doubleToLittleEndianByteArray(sampleRatePeriod));
        buf.put(Utility.intToLittleEndianByteArray(numSamples));
        buf.put(Utility.intToLittleEndianByteArray(recordCRC));
        buf.put(publicationVersion);
        buf.put((byte) (sourceIdBytes.length));
        buf.put(Utility.shortToLittleEndianByteArray(extraHeadersBytes.length));
        buf.put(Utility.intToLittleEndianByteArray(timeseriesBytes.length));
        buf.put(sourceIdBytes); // might be wrong if not ascii, should check
        buf.put(extraHeadersBytes); // might be wrong if not ascii, should check
        buf.put(timeseriesBytes);
        buf.position(0);
        return buf;
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            write(byteStream);
            byteStream.close();
            return byteStream.toByteArray();
        } catch (IOException e) {
            // shouldn't happen
            throw new RuntimeException("Caught IOException, should not happen.", e);
        }
    }

    public int getLeapSecInRecord() {
        if (getExtraHeaders().has(TIME_LEAP_SECOND)) {
            return getExtraHeaders().getInt(TIME_LEAP_SECOND);
        }
        return 0;
    }

    public void setLeapSecInRecord(int value) {
        JSONObject eh = getExtraHeaders();
        eh.put(TIME_LEAP_SECOND, value);
    }

    public void setStartDateTime(ZonedDateTime start) {
        setYear(start.getYear());
        setDayOfYear(start.getDayOfYear());
        setHour(start.getHour());
        setMinute(start.getMinute());
        setSecond(start.getSecond());
        setNanosecond(start.getNano());
    }

    public void setStartDateTime(Instant start) {
        setStartDateTime(start.atZone(TZ_UTC));
    }

    /**
     * Start time as a Date. This adjusts for possible leap seconds, but will be wrong during a leap second.
     * In particular, the returned value is the same if the seconds field is 59 or 60, as if the 59th
     * second repeats.
     */
    public ZonedDateTime getStartDateTime() {
        int sec = getSecond();
        int leaps = 0;
        if (sec > 59) {
            leaps = sec - 59;
            sec = 59;
        }
        ZonedDateTime out = ZonedDateTime.of(getYear(), 1, 1,
                getHour(), getMinute(), sec, getNanosecond(),
                TZ_UTC);
        out = out.plusDays(getDayOfYear() - 1);
        return out;
    }

    public Instant getStartInstant() {
        return getStartDateTime().toInstant();
    }

    /**
     * Checks if the record covers a time range that might contain a leap second,
     * effectively if it crosses midnight on Dec 31 or June 30. This does not imply that
     * the date actually has a leap second, just that it could.
     *
     * @return
     */
    public boolean possibleLeapSecond() {
        // first check for start on June 30 or Dec 31, allowing for leap years
        boolean leapYear = (getYear() % 4 == 0 && (getYear() % 100 != 0 || getYear() % 400 == 0));
        if ((leapYear && (getDayOfYear() == 182 || getDayOfYear() == 366))
                || (!leapYear && (getDayOfYear() == 181 || getDayOfYear() == 365))) {
            // date is June 30 or Dec 31
            ZonedDateTime start = getStartDateTime();
            ZonedDateTime end = getLastSampleTime();
            ZonedDateTime midnight = ZonedDateTime.of(start.getYear(), start.getMonthValue(), start.getDayOfMonth(), 23, 59, 59, 0, TZ_UTC);
            if (end.isAfter(midnight)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the value of startTime.
     *
     * @return Value of startTime.
     */
    public String getStartTimeString() {
        return padToLength(getYear(), 4) +"-"+ padToLength(getDayOfYear(), 3) + 'T'
                + padToLength(getHour(), 2) +":"+ padToLength(getMinute(), 2) +":"+ padToLength(getSecond(), 2)
                + nanosToString(getNanosecond()) + ZULU;
    }

    /**
     * Converts nanoseconds to string to append to time, removing trailing zeros to be 3, 6 or 9 digits.
     * Includes decimal separator if needed.
     * @param nanos
     * @return
     */
    public static String nanosToString(int nanos) {
        String nanoStr = "";
        if (nanos == 0) {
            nanoStr = "";
        } else if (nanos % 1000000 == 0) {
            nanoStr = "."+padToLength(nanos/1000000, 3);
        } else if (nanos % 1000 == 0) {
            nanoStr = "."+padToLength(nanos/1000, 6);
        }
        return nanoStr;
    }

    private static String padToLength(int val, int length) {
        String out = "" + val;
        while (out.length() < length) {
            out = "0" + out;
        }
        return out;
    }

    /**
     * the derived last sample time for this record
     * adjusted for leap seconds. In the case of a leap second in a record,
     * getLastSampleTime() minus getStartDateTime() is NOT equal to
     * (numSamples-1) * samplePeriod. Also, in case of a record ending in a leap
     * second, the seconds field in the date will be 59, ie it is as if the 59th second repeats.
     */
    public ZonedDateTime getLastSampleTime() {
        long nanoDuration = getSamplePeriodAsNanos() * (getNumSamples() - 1);
        if (getSecond() < 60) {
            nanoDuration -= (getLeapSecInRecord() * SEC_NANOS);
        }
        return getStartDateTime().plusNanos(nanoDuration);
    }


    /**
     * the predicted start time for the record following this one
     * adjusted for leap seconds. In the case of a leap second in a record,
     * getPredictedNextRecordStartTime() minus getStartDateTime() is NOT equal to
     * (numSamples) * samplePeriod. Also, in case of a record starting in a leap
     * second, the seconds field in the date will be 59, ie it is as if the 59th second repeats.
     */
    public ZonedDateTime getPredictedNextRecordStartTime() {
        long nanoDuration = getSamplePeriodAsNanos() * (getNumSamples());
        if (getSecond() < 60) {
            nanoDuration -= (getLeapSecInRecord() * SEC_NANOS);
        }
        return getStartDateTime().plusNanos(nanoDuration);
    }

    /**
     * get the time of the last sample of the record, derived from Start time, sample rate, and
     * number of samples -1.
     *
     * @return the value of end time
     */
    public String getLastSampleTimeString() {
        ZonedDateTime lst = getLastSampleTime();
        if (isEndTimeInLeapSecond()) {
            // leap second
            return DateTimeFormatter.ofPattern("yyyy-DDD'T'HH:mm:").format(lst)
                    + padToLength(lst.getSecond() + getLeapSecInRecord(), 2) + nanosToString(lst.getNano()) + ZULU;
        }
        return DateTimeFormatter.ofPattern("yyyy-DDD'T'HH:mm:ss").format(lst) + nanosToString(lst.getNano()) + ZULU;
    }

    public long getSamplePeriodAsNanos() {
        if (samplingAsRate() < 0) {
            // period in seconds
            return Math.round(-1 * ((double) samplingAsRate()) * SEC_NANOS);
        } else {
            // rate in hertz
            return Math.round(SEC_NANOS / ((double) samplingAsRate()));
        }
    }

    public boolean isStartTimeInLeapSecond() {
        return getLeapSecInRecord() > 0 && getSecond() == 60;
    }

    public boolean isEndTimeInLeapSecond() {
        ZonedDateTime start = getStartDateTime();
        long durNanos = getSamplePeriodAsNanos() * (getNumSamples() - 1);
        ZonedDateTime predLastSampleTime = start.plus(Duration.ofNanos(durNanos));
        // check if there are leap seconds, the prediected last sample time (without leaps) is
        // the next day, and is hour=0, min=0
        // or the starttime is in the leap second and the duraction is less than
        // that second
        return (getLeapSecInRecord() != 0 && (
                (!isStartTimeInLeapSecond() && start.getDayOfYear() != predLastSampleTime.getDayOfYear()
                        && predLastSampleTime.getSecond() == 0
                        && predLastSampleTime.getMinute() == 0
                        && predLastSampleTime.getHour() == 0
                ) || (isStartTimeInLeapSecond() && (start.getNano() + durNanos < SEC_NANOS))));
    }

    public boolean isTimeTagQuestionable() {
        return (getFlags() & 2) == 2;
    }

    public void setTimeTagQuestionable(boolean b) {
        if (b) {
            flags = (byte) (flags | 2);
        } else {
            flags = (byte) (flags & (255 - 2));
        }
    }

    public boolean isClockLocked() {
        return (getFlags() & 4) == 4;
    }

    public void setClockLocked(boolean b) {
        if (b) {
            flags = (byte) (flags | 4);
        } else {
            flags = (byte) (flags & (255 - 4));
        }
    }

    /**
     * Present a default string representation of the contents of this object
     *
     * @return formatted string of object contents
     */
    public String toString() {
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);
        try {
            printASCII(out, "", false);
        } catch (IOException e) {

        }
        return buf.toString();
    }

    public JSONObject getExtraHeaders() {
        if (extraHeaders == null) {
            if (getExtraHeadersAsString(0) == null || getExtraHeadersAsString(0).length() < 2) {
                extraHeaders = new JSONObject("{}");
                extraHeadersStr = null;
            } else {
                extraHeaders = new JSONObject(getExtraHeadersAsString(0));
                extraHeadersStr = null;
            }
        }
        return extraHeaders;
    }


    public String getRecordIndicator() {
        return recordIndicator;
    }


    public void setRecordIndicator(String recordIndicator) {
        this.recordIndicator = recordIndicator;
    }


    public byte getFormatVersion() {
        return formatVersion;
    }


    public void setFormatVersion(byte formatVersion) {
        this.formatVersion = formatVersion;
    }


    public byte getFlags() {
        return flags;
    }


    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public String startFieldsInUtilFormat() {
        return this.year + "," +
                String.format("%03d", this.dayOfYear) + "," +
                String.format("%02d", this.hour) + ":" +
                String.format("%02d", this.minute) + ":" +
                String.format("%02d", this.second) + "." +
                String.format("%06d", (int) Math.floor(this.nanosecond / 1000));
    }


    public int getYear() {
        return year;
    }


    public void setYear(int year) {
        this.year = year;
    }


    public int getDayOfYear() {
        return dayOfYear;
    }


    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }


    public int getHour() {
        return hour;
    }


    public void setHour(int hour) {
        this.hour = hour;
    }


    public int getMinute() {
        return minute;
    }


    public void setMinute(int minute) {
        this.minute = minute;
    }


    public int getSecond() {
        return second;
    }


    public void setSecond(int second) {
        this.second = second;
    }


    public int getNanosecond() {
        return nanosecond;
    }


    public void setNanosecond(int nanosecond) {
        this.nanosecond = nanosecond;
    }

    /**
     * Gets sample rate/period value from header. Is rate if positive, period if negative.
     * @return
     */
    public double getSampleRatePeriod() {
        return sampleRatePeriod;
    }


    /**
     * Sets sample rate/period value in header. Is rate if positive, period if negative.
     */
    public void setSampleRatePeriod(double sampleRatePeriod) {
        this.sampleRatePeriod = sampleRatePeriod;
    }


    /**
     * Calculate sample rate value from header. This is always positive, even if value is negative,
     * indicating a period in the header.
     * @return sample rate as a non-negative number (Hz)
     */
    public double samplingAsRate() {
        if (sampleRatePeriod < 0) {
            return -1.0 / sampleRatePeriod;
        } else {
            return sampleRatePeriod;
        }
    }
    /**
     * Calculate sample period value from header. This is always positive, even if value is negative,
     * indicating a period in the header.
     * @return sample period as a non-negative number (seconds)
     */
    public double samplingAsPeriod() {
        if (sampleRatePeriod < 0) {
            return -1.0 * sampleRatePeriod;
        } else {
            return 1.0/ sampleRatePeriod;
        }
    }

    public byte getTimeseriesEncodingFormat() {
        return timeseriesEncodingFormat;
    }

    public String getTimeseriesEncodingFormatName() {
        switch (timeseriesEncodingFormat) {
            case Codec.ASCII:
                return "Text";
            case Codec.SHORT:
                return "16-bit integer";
            case Codec.INT24:
                return "24-bit integer";
            case Codec.INTEGER:
                return "32-bit integer";
            case Codec.FLOAT:
                return "32-bit float (IEEE single)";
            case Codec.DOUBLE:
                return "64-bit float (IEEE double)";
            case Codec.STEIM1:
                return "STEIM-1 integer compression";
            case Codec.STEIM2:
                return "STEIM-2 integer compression";
            case 12:
                return "GEOSCOPE Muxed 24-bit integer";
            case 13:
                return "GEOSCOPE Muxed 16/3-bit gain/exp";
            case 14:
                return "GEOSCOPE Muxed 16/4-bit gain/exp";
            case 15:
                return "US National Network compression";
            case 16:
                return "CDSN 16-bit gain ranged";
            case 17:
                return "Graefenberg 16-bit gain ranged";
            case 18:
                return "IPG - Strasbourg 16-bit gain";
            case 19:
                return "STEIM-3 integer compression";
            case 30:
                return "SRO gain ranged";
            case 31:
                return "HGLP";
            case 32:
                return "DWWSSN";
            case 33:
                return "RSTN 16 bit gain ranged";
            default:
                return "Unknown";
        }
    }


    public void setTimeseriesEncodingFormat(byte timeseriesEncodingFormat) {
        this.timeseriesEncodingFormat = timeseriesEncodingFormat;
    }


    public byte getPublicationVersion() {
        return publicationVersion;
    }


    public void setPublicationVersion(byte publicationVersion) {
        this.publicationVersion = publicationVersion;
    }


    public int getNumSamples() {
        return numSamples;
    }


    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }


    public int getRecordCRC() {
        return recordCRC;
    }


    public void setRecordCRC(int recordCRC) {
        this.recordCRC = recordCRC;
    }


    public String getExtraHeadersAsString(int indent) {
        String ehStr;
        if (extraHeaders == null) {
            if (extraHeadersStr == null) {
                extraHeadersStr = "";
            }
            ehStr = extraHeadersStr;
        } else {
            ehStr = extraHeaders.toString(indent);
        }
        return ehStr;
    }


    public void setExtraHeaders(String extraHeaders) {
        this.extraHeadersStr = extraHeaders;
        this.extraHeaders = null;
    }

    public void setExtraHeaders(JSONObject extraHeaders) {
        this.extraHeaders = extraHeaders;
        this.extraHeadersStr = null;
    }

    /**
     * Gets the sourceId parsed as a FDSN source id. If the source is is not parsable as an FDSN source id, returns null
     *
     * @return
     */
    public FDSNSourceId getSourceId() {
        if (sourceId != null) {
            return sourceId;
        }
        if (sourceIdStr != null) {
            try {
                sourceId = FDSNSourceId.parse(sourceIdStr);
            } catch (FDSNSourceIdException e) {
                throw new RuntimeException("bad sourceid: "+sourceIdStr, e);
            }
        }
        return sourceId;
    }

    /**
     * Gets the raw sourceid header field as a String.
     *
     * @return
     */
    public String getSourceIdStr() {
        if (sourceId != null) {
            return sourceId.toString();
        }
        return sourceIdStr;
    }

    public void setSourceId(String sourceId) {
        this.sourceIdStr = sourceId;
        this.sourceId = null;
        this.sourceIdByteLength = (byte) this.sourceIdStr.length();
    }

    public void setSourceId(FDSNSourceId sourceId) {
        this.sourceIdStr = null;
        this.sourceId = sourceId;
        this.sourceIdByteLength = 0;
    }


    public byte getSourceIdByteLength() {
        return sourceIdByteLength;
    }

    public int calcExtraHeadersByteLength() {
        return getExtraHeaders().toString().getBytes().length;
    }

    public int getExtraHeadersByteLength() {
        return extraHeadersByteLength;
    }


    public int getDataByteLength() {
        return dataByteLength;
    }


    public byte[] getTimeseriesBytes() {
        return timeseriesBytes;
    }


    public void setTimeseriesBytes(byte[] timeseriesBytes) {
        this.timeseriesBytes = timeseriesBytes;
        this.dataByteLength = timeseriesBytes.length;
    }

    public void setTimeseries(short[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4*data.length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer fbuf = buf.asShortBuffer();
        fbuf.put(data);
        setTimeseriesBytes(buf.array());
        setTimeseriesEncodingFormat((byte) Codec.SHORT);
        setNumSamples(data.length);
    }
    public void setTimeseries(int[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4*data.length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer fbuf = buf.asIntBuffer();
        fbuf.put(data);
        setTimeseriesBytes(buf.array());
        setTimeseriesEncodingFormat((byte) Codec.INTEGER);
        setNumSamples(data.length);
    }

    public int steim1Timeseries(int[] data) throws SteimException, IOException {
        return steim1Timeseries(data, 0);
    }
    /**
     * Encodes data via Steim1, up to maxFrames number of 64 byte frames. If maxFrames is zero, encodes
     * all samples.
     * @param data
     * @param maxFrames
     * @return
     * @throws SteimException
     * @throws IOException
     */
    public int steim1Timeseries(int[] data, int maxFrames) throws SteimException, IOException {
        SteimFrameBlock sfb = Steim1.encode(data, maxFrames);
        byte[] bData = sfb.getEncodedData();
        setTimeseriesBytes(bData);
        setTimeseriesEncodingFormat((byte) Codec.STEIM1);
        setNumSamples(sfb.getNumSamples());
        return sfb.getNumSamples();
    }

    public int steim2Timeseries(int[] data) throws SteimException, IOException {
        return steim2Timeseries(data, 0);
    }
    /**
     * Encodes data via Steim2, up to maxFrames number of 64 byte frames. If maxFrames is zero, encodes
     * all samples.
     * @param data
     * @param maxFrames
     * @return
     * @throws SteimException
     * @throws IOException
     */
    public int steim2Timeseries(int[] data, int maxFrames) throws SteimException, IOException {
        SteimFrameBlock sfb = Steim2.encode(data, maxFrames);
        byte[] bData = sfb.getEncodedData();
        setTimeseriesBytes(bData);
        setTimeseriesEncodingFormat((byte) Codec.STEIM2);
        setNumSamples(sfb.getNumSamples());
        return sfb.getNumSamples();
    }
    public void setTimeseries(float[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4*data.length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fbuf = buf.asFloatBuffer();
        fbuf.put(data);
        setTimeseriesBytes(buf.array());
        setTimeseriesEncodingFormat((byte) Codec.FLOAT);
        setNumSamples(data.length);
    }
    public void setTimeseries(double[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4*data.length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer fbuf = buf.asDoubleBuffer();
        fbuf.put(data);
        setTimeseriesBytes(buf.array());
        setTimeseriesEncodingFormat((byte) Codec.DOUBLE);
        setNumSamples(data.length);
    }

    /**
     * Decompress the data in this record according to the compression type in
     * the header.
     *
     * @return
     * @throws SeedFormatException        if no blockette 1000 present
     * @throws UnsupportedCompressionType
     * @throws CodecException
     */
    public DecompressedData decompress() throws SeedFormatException, UnsupportedCompressionType, CodecException {
        // in case of record with no data, only fixed and headers, ex detection blockette,
        // which often have compression type set to 0, which messes up the decompresser
        // even though it doesn't matter since there is no data.
        if (getNumSamples() == 0) {
            return new DecompressedData(new int[0]);
        }
        Codec codec = new Codec();
        return codec.decompress(getTimeseriesEncodingFormat(),
                getTimeseriesBytes(),
                getNumSamples(),
                true);
    }

    /**
     * Gets the number of bytes for this record.
     * @return
     */
    public int getSize() {
        return FIXED_HEADER_SIZE + getSourceIdByteLength() + getExtraHeadersByteLength() + getDataByteLength();
    }

    public static final ZoneId TZ_UTC = ZoneId.of("UTC");

    public static final String ZULU = "Z";

    public static final String FDSN_PREFIX = "FDSN:";

    public static final int NETWORK_CODE_LENGTH = 8;

    public static final int STATION_CODE_LENGTH = 8;

    public static final int LOCATION_CODE_LENGTH = 8;

    public static final int CHANNEL_CODE_LENGTH = 4;

    public static final int FIXED_HEADER_SIZE = 40;

    protected static final String DEFAULT_RECORD_INDICATOR = "MS";

    protected static final byte DEFAULT_MINISEED_VERSION = (byte) 3;

    public static final long SEC_NANOS = 1000000000l;

    public static final long DAY_NANOS = 86400 * SEC_NANOS;

    public static final long LAST_SEC_NANOS = DAY_NANOS - SEC_NANOS;

    public static final int DEFAULT_START_YEAR = 1970;
    public static final int DEFAULT_START_DAY_OF_YEAR = 1;
    public static final int DEFAULT_START_HOUR = 0;
    public static final int DEFAULT_START_MINUTE = 0;
    public static final int DEFAULT_START_SECOND = 0;
    public static final int DEFAULT_START_NANOSECOND = 0;

    public static final ZonedDateTime getDefaultStartTime() {
        ZonedDateTime out = ZonedDateTime.of(DEFAULT_START_YEAR, 1, 1, DEFAULT_START_HOUR,
                DEFAULT_START_MINUTE, DEFAULT_START_SECOND, DEFAULT_START_NANOSECOND, TZ_UTC);
        out = out.plusDays(DEFAULT_START_DAY_OF_YEAR - 1);
        return out;

    }

}
