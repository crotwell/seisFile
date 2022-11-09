package edu.sc.seis.seisFile.mseed3;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.Utility;

public class MSeed3Record {

    public static final String TIME_LEAP_SECOND = "TimeLeapSecond";

    /** const for unknown data version, 0 */
    public static final int UNKNOWN_DATA_VERSION = 0;

    protected String recordIndicator = DEFAULT_RECORD_INDICATOR;

    protected byte formatVersion = DEFAULT_MINISEED_VERSION;

    protected byte flags = 0;

    protected int year = 1970;

    protected int dayOfYear = 1;

    protected int hour = 0;

    protected int minute = 0;

    protected int second = 0;

    protected int nanosecond = 0;

    protected double sampleRatePeriod = 1;
    protected double sampleRate = 1;

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
    public MSeed3Record() {}

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
    public void printASCII(PrintWriter out) throws IOException {
        printASCII(out, "");
    }

    public void printASCII(PrintWriter out, String indent) throws IOException {
        out.println(indent + getSourceId());
        out.println(indent + " start = " + getStartTimeString());
        out.println(indent + " numPTS = " + getNumSamples());
        out.println();
        out.println(indent + " RecordIndicator ('MS')= " + getRecordIndicator());
        out.println(indent + " miniSEEDversion (3)= " + getFormatVersion());
        out.println(indent + " Flags= " + getFlags());
        out.println(indent + " SampleRatePeriod = " + getSampleRatePeriod());
        out.println(indent + " TimeseriesEncodingFormat = " + getTimeseriesEncodingFormat());
        out.println(indent + " PublicationVersion = " + getPublicationVersion());
        out.println(indent + " Numberofsamples = " + getNumSamples());
        out.println(indent + " CRC­32ofdata = " + getRecordCRC());
        out.println(indent + " ChannelIdByteLength = " + getSourceIdByteLength());
        out.println(indent + " ExtraHeadersByteLength = " + getExtraHeadersByteLength());
        out.println(indent + " TimeseriesByteLength = " + getDataByteLength());
        out.println(indent + " ExtraHeaders = " + getExtraHeadersAsString());
    }
    
    public void printData(PrintWriter out) {
        byte[] d = getTimeseriesBytes();
        DecimalFormat byteFormat = new DecimalFormat("000");
        for (int i = 0; i < d.length; i++) {
            out.write(byteFormat.format(0xff & d[i]) + " ");
            if (i % 4 == 3) {out.write("  ");}
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
     * @param in
     *            miniSEED3 data stream .
     * 
     * @return an object of this class with fields filled from 'in' parameter
     * 
     * @throws IOException
     * @throws SeedFormatException
     */
    public static MSeed3Record read(DataInput in) throws IOException, SeedFormatException {
        byte[] buf = new byte[FIXED_HEADER_SIZE];
        in.readFully(buf);
        MSeed3Record header = new MSeed3Record();
        header.read(buf, 0);
        buf = new byte[header.sourceIdByteLength];
        in.readFully(buf);
        header.sourceIdStr = new String(buf, 0, header.sourceIdByteLength).trim();
        buf = new byte[header.extraHeadersByteLength];
        in.readFully(buf);
        header.extraHeadersStr = new String(buf, 0, header.extraHeadersByteLength).trim();
        header.timeseriesBytes = new byte[header.dataByteLength];
        in.readFully(header.timeseriesBytes);
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
    protected int read(byte[] buf, int offset) {
        recordIndicator = new String(buf, offset, 2);
        offset += 2;
        formatVersion = buf[offset];
        offset++;
        flags = buf[offset];
        offset++;
        year = Utility.bytesToInt(buf[offset], buf[offset+1], true);
        offset+=2;
        dayOfYear = Utility.bytesToInt(buf[offset], buf[offset+1], true);
        offset+=2;
        hour = buf[offset];
        offset++;
        minute = buf[offset];
        offset++;
        second = buf[offset];
        offset++;
        nanosecond = Utility.bytesToInt(buf[offset], buf[offset+1], buf[offset+2], buf[offset+3], true);
        offset+=4;
        sampleRatePeriod = Utility.bytesToDouble(buf, offset, true);
        offset+= 8;
        if (sampleRatePeriod < 0) {
            sampleRate = -1.0 / sampleRatePeriod;
        } else {
            sampleRate = sampleRatePeriod;
        }
        timeseriesEncodingFormat = buf[offset];
        offset++;
        publicationVersion = buf[offset];
        offset++;
        numSamples = Utility.bytesToInt(buf[offset], // careful if negative!!!
                                        buf[offset + 1],
                                        buf[offset + 2],
                                        buf[offset + 3],
                                        true);
        offset += 4;
        recordCRC = Utility.bytesToInt(buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3], true);
        offset += 4;
        sourceIdByteLength = buf[offset];
        offset++;
        extraHeadersByteLength = Utility.bytesToInt(buf[offset], buf[offset+1], true);
        offset+=2;
        dataByteLength = Utility.bytesToInt(buf[offset], buf[offset+1], true);
        offset+=2;
        return offset;
    }

    /**
     * write DataHeader contents to a DataOutput stream
     * 
     * @param dos
     *            DataOutput stream to write to
     */
    protected void write(OutputStream dos) throws IOException {
        dos.write(recordIndicator.getBytes("ASCII"));
        dos.write(formatVersion);
        dos.write(flags);
        dos.write(Utility.shortToLittleEndianByteArray(year));
        dos.write(Utility.shortToLittleEndianByteArray(dayOfYear));
        dos.write(hour);
        dos.write(minute);
        dos.write(second);
        dos.write(Utility.intToLittleEndianByteArray(nanosecond));
        dos.write(Utility.doubleToLittleEndianByteArray(sampleRatePeriod));
        dos.write(timeseriesEncodingFormat);
        dos.write(publicationVersion);
        dos.write(Utility.intToLittleEndianByteArray(numSamples));
        dos.write(Utility.intToLittleEndianByteArray(recordCRC));
        byte[] channelIdBytes = sourceId.toString().getBytes();
        dos.write((byte)(channelIdBytes.length));
        byte[] extraHeadersBytes = getExtraHeadersAsString().getBytes();
        extraHeadersByteLength = extraHeadersBytes.length;
        dos.write(Utility.shortToLittleEndianByteArray(extraHeadersBytes.length));
        dos.write(Utility.shortToLittleEndianByteArray(timeseriesBytes.length));
        dos.write(channelIdBytes); // might be wrong if not ascii, should check
        dos.write(extraHeadersBytes); // might be wrong if not ascii, should check
        dos.write(timeseriesBytes);
    }
    


    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            write(byteStream);
            byteStream.close();
            return byteStream.toByteArray();
        } catch(IOException e) {
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

    /** Start time as a Date. This adjusts for possible leap seconds, but will be wrong during a leap second.
     *  In particular, the returned value is the same if the seconds field is 59 or 60, as if the 59th
     *  second repeats.*/
    public ZonedDateTime getStartDateTime() {
        int sec = getSecond();
        int leaps = 0;
        if (sec > 59) {
            leaps = sec-59;
            sec = 59;
        }
        ZonedDateTime out = ZonedDateTime.of(getYear(), 1, 1, getHour(), getMinute(), sec, getNanosecond(), TZ_UTC);
        out = out.plusDays(getDayOfYear()-1);
        return out;
    }
    
    /**
     * Checks if the record covers a time range that might contain a leap second,
     * effectively if it crosses midnight on Dec 31 or June 30. This does not imply that
     * the date actually has a leap second, just that it could. 
     * @return
     */
    public boolean possibleLeapSecond() {
        // first check for start on June 30 or Dec 31, allowing for leap years
        boolean leapYear = (getYear() % 4 == 0 && (getYear() % 100 != 0 || getYear() % 400 == 0));
        if (       (  leapYear && ( getDayOfYear() == 182 || getDayOfYear() == 366))
                || (! leapYear && ( getDayOfYear() == 181 || getDayOfYear() == 365))) {
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
        return padToLength(getYear(), 4)+padToLength(getDayOfYear(),3)+'T'
                +padToLength(getHour(),2)+padToLength(getMinute(),2)+padToLength(getSecond(),2)
                +'.'+padToLength(getNanosecond(), 9)+ZULU;
    }
    
    private String padToLength(int val, int length) {
        String out = ""+val;
        while (out.length() < length) {
            out = "0"+out;
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
        if (getSecond() < 60 ) {
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
        long nanoDuration = getSamplePeriodAsNanos() * (getNumSamples() );
        if (getSecond() < 60 ) {
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
        if (isEndTimeInLeapSecond() ) {
            // leap second
            ZonedDateTime lst = getLastSampleTime();
            return DateTimeFormatter.ofPattern("yyyyDDD'T'HHmm").format(lst)
                    +padToLength(lst.getSecond()+getLeapSecInRecord(), 2)+"."+padToLength(lst.getNano()/1000, 6)+ZULU;
        }
        return DateTimeFormatter.ofPattern("yyyyDDD'T'HHmmss.SSSSSS").format(getLastSampleTime())+ZULU;
    }
    
    public long getSamplePeriodAsNanos() {
        if (getSampleRate() < 0) {
            // period in seconds
            return Math.round(-1 * ((double)getSampleRate()) * SEC_NANOS);
        } else {
            // rate in hertz
            return Math.round(SEC_NANOS / ((double)getSampleRate()));
        }
    }

    public boolean isStartTimeInLeapSecond() {
      return getLeapSecInRecord() > 0 && getSecond() == 60;
    }
    
    public boolean isEndTimeInLeapSecond() {
        ZonedDateTime start = getStartDateTime();
        long durNanos = getSamplePeriodAsNanos() * (getNumSamples() - 1 );
        ZonedDateTime predLastSampleTime = start.plus(Duration.ofNanos(durNanos));
        // check if there are leap seconds, the prediected last sample time (without leaps) is
        // the next day, and is hour=0, min=0
        // or the starttime is in the leap second and the duraction is less than
        // that second
        return (getLeapSecInRecord() != 0 && (
                ( ! isStartTimeInLeapSecond() && start.getDayOfYear() != predLastSampleTime.getDayOfYear() 
                        && predLastSampleTime.getSecond() == 0 
                        && predLastSampleTime.getMinute() == 0 
                        && predLastSampleTime.getHour() == 0
                ) || ( isStartTimeInLeapSecond() && (start.getNano() + durNanos < SEC_NANOS))));  
    }

    public boolean isTimeTagQuestionable() {
        return (getFlags() & 2) == 2;
    }

    public void setTimeTagQuestionable(boolean b) {
        if (b) {
            flags = (byte)(flags | 2);
        } else {
            flags = (byte)(flags & (255 - 2));
        }
    }

    public boolean isClockLocked() {
        return (getFlags() & 4) == 4;
    }

    public void setClockLocked(boolean b) {
        if (b) {
            flags = (byte)(flags | 4);
        } else {
            flags = (byte)(flags & (255 - 4));
        }
    }

    /**
     * Present a default string representation of the contents of this object
     * 
     * @return formatted string of object contents
     */
    public String toString() {
        String s =  getSourceId() + "."
                + getStartTimeString() + "  " + getSampleRate() +" "+ getNumSamples();
        return s;
    }

    public JSONObject getExtraHeaders() {
        if (extraHeaders == null) {
            if (getExtraHeadersAsString() == null || getExtraHeadersAsString().length() < 2) {
                extraHeaders = new JSONObject("{}");
                extraHeadersStr = null;
            } else {
                extraHeaders = new JSONObject(getExtraHeadersAsString());
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

    
    public double getSampleRatePeriod() {
        return sampleRatePeriod;
    }

    
    public void setSampleRatePeriod(double sampleRatePeriod) {
        this.sampleRatePeriod = sampleRatePeriod;
    }

    
    public double getSampleRate() {
        return sampleRate;
    }

    
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    
    public byte getTimeseriesEncodingFormat() {
        return timeseriesEncodingFormat;
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

    
    public String getExtraHeadersAsString() {
        String ehStr;
        if (extraHeaders == null) {
            if (extraHeadersStr == null) {
                extraHeadersStr = "";
            }
            ehStr = extraHeadersStr;
        } else {
            ehStr = extraHeaders.toString();
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


    public FDSNSourceId getSourceId() {
        return sourceId;
    }


    public void setSourceId(String sourceId) {
        this.sourceIdStr = sourceId;
        this.sourceIdByteLength = (byte)this.sourceIdStr.length();
    }

    public void setSourceId(FDSNSourceId sourceId) {
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
    
    public int getSize() {
        return FIXED_HEADER_SIZE+ getSourceIdByteLength()+getExtraHeadersByteLength()+getDataByteLength();
    }

    public static final ZoneId TZ_UTC = ZoneId.of("UTC");
    
    public static final String ZULU = "Z";

    public static final String FDSN_PREFIX = "FDSN:";
    
    public static final int NETWORK_CODE_LENGTH = 8;

    public static final int STATION_CODE_LENGTH = 8;

    public static final int LOCATION_CODE_LENGTH = 8;

    public static final int CHANNEL_CODE_LENGTH = 4;

    public static final int FIXED_HEADER_SIZE = 38;

    protected static final String DEFAULT_RECORD_INDICATOR = "MS";

    protected static final byte DEFAULT_MINISEED_VERSION = (byte)3;
    
    public static final long SEC_NANOS = 1000000000l;
    
    public static final long DAY_NANOS = 86400 * SEC_NANOS;
    
    public static final long LAST_SEC_NANOS = DAY_NANOS - SEC_NANOS;
    
}
