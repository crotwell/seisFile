package edu.sc.seis.seisFile.mseed3;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public static final byte UNKNOWN_DATA_VERSION = 0;

    protected String recordIndicator = DEFAULT_RECORD_INDICATOR;

    protected byte formatVersion = DEFAULT_MINISEED_VERSION;

    protected byte flags = 0;

    protected int year;

    protected int dayOfYear;

    protected int hour;

    protected int minute;

    protected int second;

    protected int nanosecond;

    protected double sampleRatePeriod;
    protected double sampleRate;

    protected byte timeseriesEncodingFormat;

    protected byte publicationVersion;

    protected int numSamples;

    protected int recordCRC;

    protected byte channelIdByteLength;
    
    protected int extraHeadersByteLength;

    protected int dataByteLength;
    
    protected String channelId;
    
    protected String[] fdsnParsedChannelId;

    protected String extraHeaders;
    
    protected JSONObject extraHeadersJSON;
    
    protected byte[] timeseriesBytes;

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
        out.println(indent + getChannelId());
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
        out.println(indent + " ChannelIdByteLength = " + getChannelIdByteLength());
        out.println(indent + " ExtraHeadersByteLength = " + getExtraHeadersByteLength());
        out.println(indent + " TimeseriesByteLength = " + getDataByteLength());
        out.println(indent + " ExtraHeaders = " + getExtraHeaders());
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
        buf = new byte[header.channelIdByteLength];
        in.readFully(buf);
        header.channelId = new String(buf, 0, header.channelIdByteLength).trim();
        buf = new byte[header.extraHeadersByteLength];
        in.readFully(buf);
        header.extraHeaders = new String(buf, 0, header.extraHeadersByteLength).trim();
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
        channelIdByteLength = buf[offset];
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
        byte[] channelIdBytes = channelId.getBytes();
        dos.write((byte)(channelIdBytes.length));
        byte[] extraHeadersBytes = extraHeaders.getBytes();
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

    /**
     * the derived last sample time for this record
     * adjusted for leap seconds.
     */
    public ZonedDateTime getLastSampleTime() {
        long nanoDuration = getSamplePeriodAsNanos() * (getNumSamples() - 1)
                - (getLeapSecInRecord() * SEC_NANOS);
        return getStartTimeDate().plus( Duration.ofNanos(nanoDuration));
    }

    public int getLeapSecInRecord() {
        if (getExtraHeadersJSON().has(TIME_LEAP_SECOND)) {
                return getExtraHeadersJSON().getInt(TIME_LEAP_SECOND);
        }
        return 0;
    }

    /** Start time as a Date. This does NOT adjust for possible leap secs. */
    public ZonedDateTime getStartTimeDate() {
        return ZonedDateTime.of(getYear(), 0, getDayOfYear(), getHour(), getMinute(), getSecond(), getNanosecond(), ZoneId.of("UTC"));
    }

    /**
     * Get the value of startTime.
     * 
     * @return Value of startTime.
     */
    public String getStartTimeString() {
        return padToLength(getYear(), 4)+padToLength(getDayOfYear(),3)+'T'
                +padToLength(getHour(),2)+padToLength(getMinute(),2)+padToLength(getSecond(),2)
                +'.'+padToLength(getNanosecond(), 9);
    }
    
    private String padToLength(int val, int length) {
        String out = ""+val;
        while (out.length() < length) {
            out = "0"+out;
        }
        return out;
    }

    /**
     * get the value of end time. derived from Start time, sample rate, and
     * number of samples.
     * 
     * @return the value of end time
     */
    public String getLastSampleTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyDDD'T'HHmmss.SSSZ");
        return sdf.format(getLastSampleTime());
    }

    /*
     * public String getPredictedNextStartTimeString() { long time =
     * getPredictedNextStartTime(); boolean isLeapSec = false; if
     * (isPosLeapSecInRecord() && time % 86400 == 1) { isLeapSec = true; }
     * return longToDateString(time, isLeapSec); }
     */
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
        ZonedDateTime start = getStartTimeDate();
        long durNanos = getSamplePeriodAsNanos() * (getNumSamples() - 1 );
        ZonedDateTime predLastSampleTime = start.plus(Duration.ofNanos(durNanos));
        // check if there are leap seconds, the prediected last sample time (without leaps) is
        // the next day, and is hour=0, min=0
        // or the starttime is in the leap second and the duraction is less than
        // that second
        return (getLeapSecInRecord() != 0 && (
                (start.getDayOfYear() != predLastSampleTime.getDayOfYear() 
                && predLastSampleTime.getMinute() == 0 && predLastSampleTime.getHour() == 0
                ) || (
                        isStartTimeInLeapSecond() && (start.getNano() + predLastSampleTime.getNano() < SEC_NANOS))));  
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
        String s =  getChannelId() + "."
                + getStartTimeString() + "  " + getSampleRate() +" "+ getNumSamples();
        return s;
    }

    public JSONObject getExtraHeadersJSON() {
        if (extraHeadersJSON == null) {
            if (getExtraHeaders().length() < 2) {
                extraHeadersJSON = new JSONObject("{}");
            } else {
                extraHeadersJSON = new JSONObject(getExtraHeaders());
            }
        }
        return extraHeadersJSON;
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

    
    public String getExtraHeaders() {
        return extraHeaders;
    }

    
    public void setExtraHeaders(String extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    
    public String getChannelId() {
        return channelId;
    }
    
    public String getNetworkCode() {
        return parseChannelId()[0];
    }
    
    public String getStationCode() {
        return parseChannelId()[1];
    }
    
    public String getLocationCode() {
        return parseChannelId()[2];
    }
    
    public String getChannelCode() {
        return parseChannelId()[3];
    }
    
    public String[] parseChannelId() {
        if (fdsnParsedChannelId != null) {
            return fdsnParsedChannelId;
        }
        if (channelId.startsWith(FDSN_PREFIX)) {
            String tmp = channelId.substring(FDSN_PREFIX.length());
            String[] pieces = tmp.split("\\.");
            String[] locChan;
            if (pieces[2].contains(":")) {
                // has loc code
                locChan = pieces[2].split(":");
            } else {
                locChan = new String[] {"", pieces[2]};
            }
            String[] out = new String[4];
            out[0] = pieces[0];
            out[1] = pieces[1];
            out[2] = locChan[0];
            out[3] = locChan[1];
            return out;
        }
        throw new RuntimeException("Unknown channel id prefix: "+channelId);
    }

    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
        this.channelIdByteLength = (byte)this.channelId.length();
    }

    
    public byte getChannelIdByteLength() {
        return channelIdByteLength;
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
        return FIXED_HEADER_SIZE+getChannelIdByteLength()+getExtraHeadersByteLength()+getDataByteLength();
    }

    public static final TimeZone TZ_UTC = TimeZone.getTimeZone("UTC");

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
