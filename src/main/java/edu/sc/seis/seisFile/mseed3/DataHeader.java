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

public class DataHeader {

    protected String recordIndicator = DEFAULT_RECORD_INDICATOR;
    
    protected byte miniSeedVersion = DEFAULT_MINISEED_VERSION;
    
    protected String networkCode;
    
    protected String stationCode;

    protected String locationCode;

    protected String channelCode;

    protected byte qualityIndicator;

    protected byte dataVersion;
    
    protected int recordLength;
    
    protected long startTime;

    protected int numSamples;

    protected float sampleRate;
    
    protected int dataCRC;
    
    protected int dataOffset;

    protected byte flags;

    protected byte sampleEncodingFormat;

    protected byte numOpaqueHeaders;

    protected String[] opaqueHeaders;

    /**
     * creates a DataHeader object with nothing set (ie nulls and zeros)
     */
    public DataHeader() {
    }

    /**
     * Writes an ASCII version of the record header. This is not meant to be a definitive ascii representation,
     * merely to give something to print for debugging purposes. Ideally each field of the header should
     * be printed in the order is appears in the header in a visually appealing way.
     * 
     * @param out
     *            a Writer
     * 
     */
    public void writeASCII(PrintWriter out) throws IOException {
        writeASCII(out, "");
    }

    public void writeASCII(PrintWriter out, String indent) throws IOException {
        out.print(indent+getNetworkCode().trim()+"."+getStationCode().trim()+"."+getChannelCode());
        out.print(" start=" + getStartTime());
        out.print(" numPTS=" + getNumSamples());
        out.print(" dataOffset=" + getDataOffset());
        
        out.print(indent+"RecordIndicator="+getRecordIndicator()+"='MS'");
        out.print(indent+"miniSEEDversion="+getMiniSeedVersion()+"=3");
        out.print(indent+"Networkcode="+getNetworkCode().trim());
        out.print(indent+"Stationcode="+getStationCode().trim());
        out.print(indent+"Locationcodes="+getLocationCode().trim());
        out.print(indent+"Channelcodes="+getChannelCode().trim());
        out.print(indent+"QualityIndicator="+getQualityIndicator());
        out.print(indent+"DataVersionindicator="+getDataVersion());
        out.print(indent+"Recordlength="+getRecordLength());
        out.print(indent+"Recordstarttime="+getStartTime()+" "+getStartTimeString());
        out.print(indent+"Numberofsamples="+getNumSamples());
        out.print(indent+"Samplerate="+getSampleRate());
        out.print(indent+"CRC­32ofdata="+getDataCRC());
        out.print(indent+"Offsettodata="+getDataOffset());
        out.print(indent+"Flags="+getFlags());
        out.print(indent+"sampleEncodingFormat="+getDataEncodingFormat());
        out.print(indent+"Numberofopaqueheadersthatfollow="+getNumOpaqueHeaders());
        for (int i = 0; i < getNumOpaqueHeaders(); i++) {
            out.print(indent+getOpaqueHeader(i));
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
    public static DataHeader read(DataInput in) throws IOException,
            SeedFormatException {
        byte[] buf = new byte[53];
        in.readFully(buf);
        DataHeader data = new DataHeader();
        data.read(buf, 0);
        int endOpaque = data.getDataOffset();
        if (data.getDataOffset() == 0) {
            endOpaque = data.getRecordLength();
        }
        byte[] opaqueTemp = new byte[endOpaque-data.getFixedSize()];
        in.readFully(opaqueTemp);
        String opaque = new String(opaqueTemp);
        String[] opaqueArray = opaque.split(END_OPAQUE);
        if (opaqueArray.length != data.getNumOpaqueHeaders()) {
            throw new SeedFormatException("Num opaque headers doesn't match number of opaque headers: expected "
                                          +data.getNumOpaqueHeaders()+" found: "+opaqueArray.length);
        }
        for (int i = 0; i < opaqueArray.length; i++) {
            // trim the '~'
            opaqueArray[i] = opaqueArray[i].substring(0, opaqueArray[i].length()-1);
        }
        data.setOpaqueHeaders(opaqueArray);
        return data;
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
        flags = buf[offset+FLAGS_OFFSET];
        boolean byteSwapFlag = (flags & 0x1) == 0x0; // bit 0 ==1 means big endian, ==0 means little endian
        
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
        qualityIndicator = buf[offset];
        offset++;
        dataVersion = buf[offset];
        offset++;
        recordLength = Utility.bytesToInt(buf[offset],
                                          buf[offset + 1],
                                          buf[offset + 2],
                                          buf[offset + 3],
                                          byteSwapFlag);
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
        numSamples = Utility.bytesToInt(buf[offset],    // careful if negative!!!
                                         buf[offset + 1],
                                         buf[offset + 2],
                                         buf[offset + 3],
                                         byteSwapFlag);
        offset += 4;
        sampleRate = Utility.bytesToFloat(buf, offset + 24,
                                          byteSwapFlag);
        offset += 4;
        dataCRC = Utility.bytesToInt(buf[offset],
                                         buf[offset + 1],
                                         buf[offset + 2],
                                         buf[offset + 3],
                                         byteSwapFlag);
        offset += 4;
        dataOffset = Utility.uBytesToInt(buf[offset + 36],
                                         buf[offset + 37],
                                         byteSwapFlag);
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
        dos.write(Utility.pad(getNetworkCode().getBytes("ASCII"),
                              NETWORK_CODE_LENGTH,
                              (byte)32));
        dos.write(Utility.pad(getStationCode().getBytes("ASCII"),
                              STATION_CODE_LENGTH,
                              (byte)32));
        dos.write(Utility.pad(getLocationCode().getBytes("ASCII"),
                              LOCATION_CODE_LENGTH,
                              (byte)32));
        dos.write(Utility.pad(getChannelCode().getBytes("ASCII"),
                              CHANNEL_CODE_LENGTH,
                              (byte)32));
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
            dos.write((opaqueHeaders[i]+END_OPAQUE).getBytes("ASCII"));
        }
    }

    public short getFixedSize() {
        return FIXED_HEADER_SIZE;
    }
    
    public int getSizeWithOpaque() {
        int out = getFixedSize();
        for (int i = 0; i < opaqueHeaders.length; i++) {
            out += opaqueHeaders[i].length();
        }
        return out;
    }
    

    /** returns the predicted start time of the next record, ie begin + numSample*period
     * 
     */
    public long getPredictedNextStartTime() {
        return getStartTime()+getSamplePeriodAsMicros()*getNumSamples();
    }
    
    public long[] getTimeRange() {
        return new long[] { getStartTime(), getLastSampleTime()};
    }
    
    /**
     * return a Btime structure containing the derived last sample time for this
     * record
     */
    public long getLastSampleTime() {
        return getPredictedNextStartTime()-getSamplePeriodAsMicros();
    }
    

    public Date getStartTimeDate() {
        return new Date(getStartTime()/1000);
    }

    /**
     * Get the value of startTime.
     * 
     * @return Value of startTime.
     */
    public String getStartTimeString() {
        return longToDateString(getStartTime());
        
    }
    
    /**
     * get the value of end time. derived from Start time, sample rate, and
     * number of samples.
     * 
     * @return the value of end time
     */
    public String getLastSampleTimeString() {
        return longToDateString(getLastSampleTime());
    }
    
    public String getPredictedNextStartTimeString() {
        return longToDateString(getPredictedNextStartTime());
    }

    public long getSamplePeriodAsMicros() {
        if (getSampleRate() < 0) {
            // period in seconds
            return Math.round(-1*((double)getSampleRate())*1000000d);
        } else {
         // rate in hertz
            return Math.round(1000000d/((double)getSampleRate()));
        }
    }
    
    /**
     * Present a default string representation of the contents of this object
     * 
     * @return formatted string of object contents
     */
    public String toString() {
        String s = super.toString() + " ";
        s += " "+ getNetworkCode() + "." + getStationCode() + "." + getLocationCode() + "." + getChannelCode() + "." 
                + getStartTimeString() + "  " + getSampleRate()*getNumSamples() + " "
                + " " + getDataOffset() ;
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
        return channelCode;
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

    
    public byte getQualityIndicator() {
        return qualityIndicator;
    }

    
    public void setQualityIndicator(byte qualityIndicator) {
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
        this.opaqueHeaders = opaqueHeaders;
    }

    
    public int getDataOffset() {
        return dataOffset;
    }
    
    protected static String longToDateString(long longMicros) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");
        sdf.setTimeZone(TZ_UTC);
        String out = sdf.format(new Date(longMicros / 1000)); // micros to millis
        int micros = (int)( longMicros % 1000);
        if (micros < 10) {
            return out+"00"+micros;
        } else if (micros < 100) {
            return out+"0"+micros;
        } else {
            return out+micros;
        }
    }

    public static final TimeZone TZ_UTC = TimeZone.getTimeZone("UTC");
    
    public static final int NETWORK_CODE_LENGTH = 2;
    
    public static final int STATION_CODE_LENGTH = 5;
    
    public static final int LOCATION_CODE_LENGTH = 2;
    
    public static final int CHANNEL_CODE_LENGTH = 3;
    
    public static final int FIXED_HEADER_SIZE = 30 + NETWORK_CODE_LENGTH+STATION_CODE_LENGTH+LOCATION_CODE_LENGTH+CHANNEL_CODE_LENGTH;
    
    public static final int FLAGS_OFFSET = 4;

    protected static final String DEFAULT_RECORD_INDICATOR = "MS";
    
    protected static final byte DEFAULT_MINISEED_VERSION = (byte)3;
    
    public static final String END_OPAQUE = "~";
}
