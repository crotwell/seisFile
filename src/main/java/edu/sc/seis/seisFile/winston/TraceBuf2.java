package edu.sc.seis.seisFile.winston;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.Steim1;
import edu.iris.dmc.seedcodec.SteimException;
import edu.iris.dmc.seedcodec.SteimFrameBlock;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataTooLargeException;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.Utility;

public class TraceBuf2 {

    TraceBuf2(int pin,
                     int numSamples,
                     double startTime,
                     double endTime,
                     double sampleRate,
                     String station,
                     String network,
                     String channel,
                     String locId,
                     String version,
                     String dataType,
                     short quality,
                     short pad) {
        super();
        this.pin = pin;
        this.numSamples = numSamples;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sampleRate = sampleRate;
        this.station = station;
        this.network = network;
        this.channel = channel;
        this.locId = locId;
        if ("  ".equals(locId)) {this.locId = "--";}
        this.version = version;
        this.dataType = dataType;
        this.quality = quality;
        this.pad = pad;
        if (network.length() > 8) {
            throw new IllegalArgumentException("network cannot be longer than 8 chars: "+network.length()+" '"+network+"'");
        }
        if (station.length() > 6) {
            throw new IllegalArgumentException("station cannot be longer than 6 chars: "+station.length()+" '"+station+"'");
        }
        if (locId.length() > 2) {
            throw new IllegalArgumentException("locId cannot be longer than 2 chars: "+locId.length()+" '"+locId+"'");
        }
        if (channel.length() > 3) {
            throw new IllegalArgumentException("channel cannot be longer than 3 chars: "+channel.length()+" '"+channel+"'");
        }
    }

    public TraceBuf2(int pin,
                     int numSamples,
                     double startTime,
                     double sampleRate,
                     String station,
                     String network,
                     String channel,
                     String locId,
                     short[] data) {
        this(pin, 
             numSamples, 
             startTime, 
             startTime+(numSamples-1)/sampleRate, 
             sampleRate, 
             station, 
             network,
             channel, 
             locId, 
             TRACEBUF_VERSION, 
             SUN_IEEE_SHORT_INTEGER, 
             S_ZERO, S_ZERO);
        this.shortData = data;
    }

    public TraceBuf2(int pin,
                     int numSamples,
                     double startTime,
                     double sampleRate,
                     String station,
                     String network,
                     String channel,
                     String locId,
                     int[] intData) {
        this(pin, numSamples,
             startTime,
             startTime+(numSamples-1)/sampleRate,
             sampleRate,
             station,
             network,
             channel,
             locId,
             TRACEBUF_VERSION, SUN_IEEE_INTEGER,
             S_ZERO, S_ZERO);
        this.intData = intData;
    }

    public TraceBuf2(int pin,
                     int numSamples,
                     double startTime,
                     double sampleRate,
                     String station,
                     String network,
                     String channel,
                     String locId,
                     float[] data) {
        this(pin, numSamples,
             startTime,
             startTime+(numSamples-1)/sampleRate,
             sampleRate,
             station,
             network,
             channel,
             locId,
             TRACEBUF_VERSION,
             SUN_IEEE_SINGLE_PRECISION_REAL,
             S_ZERO, S_ZERO);
        this.floatData = data;
    }

    public TraceBuf2(int pin,
                     int numSamples,
                     double startTime,
                     double sampleRate,
                     String station,
                     String network,
                     String channel,
                     String locId,
                     double[] data) {
        this(pin, numSamples,
             startTime,
             startTime+(numSamples-1)/sampleRate,
             sampleRate,
             station,
             network,
             channel,
             locId,
             TRACEBUF_VERSION, SUN_IEEE_DOUBLE_PRECISION_REAL,
             S_ZERO, S_ZERO);
        this.doubleData = data;
    }

    public TraceBuf2(byte[] data) {
        dataType = extractDataType(data);
        boolean swapBytes =  isSwapBytes(dataType);
        pin = Utility.bytesToInt(data, 0, swapBytes);
        numSamples = extractNumSamples(data, swapBytes);
        startTime = Utility.bytesToDouble(data, 8, swapBytes);
        endTime = Utility.bytesToDouble(data, 16, swapBytes);
        sampleRate = Utility.bytesToDouble(data, 24, swapBytes);
        station = Utility.extractNullTermString(data, 32, 7);
        network = Utility.extractNullTermString(data, 39, 9);
        channel = Utility.extractNullTermString(data, 48, 4);
        locId = Utility.extractNullTermString(data, 52, 3);
        if ("  ".equals(locId)) {locId = "--";}
        version = Utility.extractString(data, 55, 2);
        // dataType already extract above: Utility.extractNullTermString(data, 57, 3);
        quality = Utility.bytesToShort(data[60], data[61], swapBytes);
        pad = Utility.bytesToShort(data[62], data[63], swapBytes);
        int offset = 64;
        
        if (isShortData()) {
            shortData = new short[numSamples];
            for (int i = 0; i < shortData.length; i++) {
                shortData[i] = Utility.bytesToShort(data[offset], data[offset + 1], swapBytes);
                offset += 2;
            }
            dataType = SUN_IEEE_SHORT_INTEGER;
        } else if (isIntData()) {
            intData = new int[numSamples];
            for (int i = 0; i < intData.length; i++) {
                intData[i] = Utility.bytesToInt(data[offset],
                                                data[offset + 1],
                                                data[offset + 2],
                                                data[offset + 3],
                                                swapBytes);
                offset += 4;
            }
            dataType = SUN_IEEE_INTEGER;
        } else if (isFloatData()) {
            floatData = new float[numSamples];
            for (int i = 0; i < floatData.length; i++) {
                floatData[i] = Utility.bytesToFloat(data[offset],
                                                    data[offset + 1],
                                                    data[offset + 2],
                                                    data[offset + 3],
                                                    swapBytes);
                offset += 4;
            }
            dataType = SUN_IEEE_SINGLE_PRECISION_REAL;
        } else if (isDoubleData()) {
            doubleData = new double[numSamples];
            for (int i = 0; i < doubleData.length; i++) {
                doubleData[i] = Utility.bytesToDouble(data[offset],
                                                      data[offset + 1],
                                                      data[offset + 2],
                                                      data[offset + 3],
                                                      data[offset + 4],
                                                      data[offset + 5],
                                                      data[offset + 6],
                                                      data[offset + 7],
                                                      swapBytes);
                offset += 8;
            }
            dataType = SUN_IEEE_DOUBLE_PRECISION_REAL;
        } else if (dataType.equals(NORESS_GAIN_RANGED)) {
            throw new RuntimeException("NORESS gain-ranged data type not supported: " + dataType);
        } else {
            throw new RuntimeException("Unknown data type: " + dataType);
        }
    }

    public static int extractNumSamples(byte[] data, boolean swapBytes) {
        return Utility.bytesToInt(data, 4, swapBytes);
    }

    public static String extractDataType(byte[] data) {
        return Utility.extractNullTermString(data, 57, 3);
    }
    
    public static boolean isSwapBytes(String dataType) {
        return dataType.equals(INTEL_IEEE_DOUBLE_PRECISION_REAL) || dataType.equals(INTEL_IEEE_INTEGER)
                || dataType.equals(INTEL_IEEE_SHORT_INTEGER) || dataType.equals(INTEL_IEEE_SINGLE_PRECISION_REAL);
    }

    public static int getSampleSize(String dataType) {
        if(TraceBuf2.isShortData(dataType)) {
            return 2;
        } else if(TraceBuf2.isIntData(dataType) || TraceBuf2.isFloatData(dataType)) {
            return 4;
        } else if(TraceBuf2.isDoubleData(dataType)) {
            return 8;
        }
        throw new RuntimeException("Unknown dataType: '"+dataType+"'");
    }
    
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(pin);
        out.writeInt(numSamples);
        out.writeDouble(startTime);
        out.writeDouble(endTime);
        out.writeDouble(sampleRate);

        Utility.writeNullTermString(station, MAX_STA_LEN, out);
        Utility.writeNullTermString(network, MAX_NET_LEN, out);
        Utility.writeNullTermString(channel, MAX_CHAN_LEN, out);
        Utility.writeNullTermString(locId, MAX_LOC_LEN, out);

        Utility.writeNullTermString(version, 2, out);
        Utility.writeNullTermString(dataType, 3, out);
        out.writeShort(quality);
        out.writeShort(pad);

        if (isShortData()) {
            for (int i = 0; i < shortData.length; i++)
                out.writeShort(shortData[i]);
        } else if (isIntData()) {
            for (int i = 0; i < intData.length; i++)
                out.writeInt(intData[i]);
        } else if (isFloatData()) {
            for (int i = 0; i < floatData.length; i++)
                out.writeFloat(floatData[i]);
        } else if (isDoubleData()) {
            for (int i = 0; i < doubleData.length; i++)
                out.writeDouble(doubleData[i]);
        }
    }
    
    public List<TraceBuf2> split(int maxSize) {
        if (maxSize <= 0) {
            return Arrays.asList(new TraceBuf2[] { this });
        }
        if (maxSize <= HEADER_SIZE) {
            throw new IllegalArgumentException("maxSize cannot be smaller than header size ("+HEADER_SIZE+") but was "+maxSize);
        }
        List<TraceBuf2> out = new ArrayList<TraceBuf2>();
        if (getSize() <= maxSize) {
            out.add(this);
        } else {
            int maxSamplesPerTB = (maxSize-HEADER_SIZE)/getSampleSize(getDataType());
            int curSample = 0;
            while(curSample < numSamples) {
                int splitPoints = Math.min(maxSamplesPerTB, numSamples-curSample);
                if (splitPoints == maxSamplesPerTB) {
                        splitPoints = Math.min(maxSamplesPerTB, (numSamples-curSample+1)/2);
                }
                double splitStartTime = startTime+(curSample )/sampleRate;
                double splitEndTime = startTime+(curSample+splitPoints -1)/sampleRate;
                TraceBuf2 first = new TraceBuf2( pin,
                                                 splitPoints,
                                                 splitStartTime,
                                                 splitEndTime,
                                                 sampleRate,
                                                 station,
                                                 network,
                                                 channel,
                                                 locId,
                                                 version,
                                                 dataType,
                                                 quality,
                                                 pad);
                out.add(first);
                if (isShortData()) {
                    first.shortData = new short[splitPoints];
                    System.arraycopy(shortData, curSample, first.shortData, 0, splitPoints);
                } else if (isIntData()) {
                    first.intData = new int[splitPoints];
                    System.arraycopy(intData, curSample, first.intData, 0, splitPoints);
                } else if (isFloatData()) {
                    first.floatData = new float[splitPoints];
                    System.arraycopy(floatData, curSample, first.floatData, 0, splitPoints);
                } else if (isDoubleData()) {
                    first.doubleData = new double[splitPoints];
                    System.arraycopy(doubleData, curSample, first.doubleData, 0, splitPoints);
                } else {
                    throw new RuntimeException("Unknown data type: "+getDataType());
                }
                curSample += splitPoints;
            }
        }
        return out;
    }
    
    /** Pin number */
    int pin;

    /** Number of samples in packet */
    int numSamples;

    /**
     * time of first sample in epoch seconds (seconds since midnight 1/1/1970)
     */
    double startTime;

    /** Time of last sample in epoch seconds */
    double endTime;

    /** Sample rate; nominal */
    double sampleRate;

    /** Site name (NULL-terminated) */
    String station;

    /** Network name (NULL-terminated) */
    String network;

    /** Component/channel code (NULL-terminated) */
    String channel;

    /** Location code (NULL-terminated) */
    String locId;

    /** version field */
    String version;

    /** Data format code (NULL-terminated) */
    String dataType;

    /** Data-quality field */
    short quality;

    /** padding */
    short pad;

    public boolean isShortData() {
        return isShortData(dataType);
    }

    public boolean isIntData() {
        return isIntData(dataType);
    }

    public boolean isFloatData() {
        return isFloatData(dataType);
    }

    public boolean isDoubleData() {
        return isDoubleData(dataType);
    }

    public static boolean isShortData(String dataType) {
        return dataType.equals(INTEL_IEEE_SHORT_INTEGER) || dataType.equals(SUN_IEEE_SHORT_INTEGER);
    }

    public static boolean isIntData(String dataType) {
        return dataType.equals(INTEL_IEEE_INTEGER) || dataType.equals(SUN_IEEE_INTEGER);
    }

    public static boolean isFloatData(String dataType) {
        return dataType.equals(INTEL_IEEE_SINGLE_PRECISION_REAL) || dataType.equals(SUN_IEEE_SINGLE_PRECISION_REAL);
    }

    public static boolean isDoubleData(String dataType) {
        return dataType.equals(INTEL_IEEE_DOUBLE_PRECISION_REAL) || dataType.equals(SUN_IEEE_DOUBLE_PRECISION_REAL);
    }

    public int getPin() {
        return pin;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public Date getStartDate() {
        return new Date(Math.round(getStartTime()*1000)); // convert from seconds to milliseconds
    }

    public Date getEndDate() {
        return new Date(Math.round(getEndTime()*1000)); // convert from seconds to milliseconds
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public String getStation() {
        return station;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    public String getLocId() {
        return locId;
    }

    public String getVersion() {
        return version;
    }

    public String getDataType() {
        return dataType;
    }

    public short getQuality() {
        return quality;
    }

    public short getPad() {
        return pad;
    }

    public short[] getShortData() {
        return shortData;
    }

    public int[] getIntData() {
        if (isIntData()) {
            return intData;
        } else if (isShortData()) {
            int[] out = new int[shortData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = shortData[i];
            }
            return out;
        }
        return null;
    }

    public float[] getFloatData() {
        if (isFloatData()) {
            return floatData;
        } else if (isIntData()) {
            float[] out = new float[intData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = intData[i];
            }
            return out;
        } else if (isShortData()) {
            float[] out = new float[shortData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = shortData[i];
            }
            return out;
        }
        return null;
    }

    public double[] getDoubleData() {
        if (isDoubleData()) {
            return doubleData;
        } else if (isFloatData()) {
            double[] out = new double[floatData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = floatData[i];
            }
            return out;
        } else if (isIntData()) {
            double[] out = new double[intData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = intData[i];
            }
            return out;
        } else if (isShortData()) {
            double[] out = new double[shortData.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = shortData[i];
            }
            return out;
        }
        return null;
    }
    
    public byte getSeedEncoding() {
        if (dataType.equals("s4") || dataType.equals("i4")) {
            return B1000Types.INTEGER;
        } else if (dataType.equals("s2") || dataType.equals("i2")) {
            return B1000Types.SHORT;
        } else if (dataType.equals("t4") || dataType.equals("f4")) {
            return B1000Types.FLOAT;
        } else if (dataType.equals("t8") || dataType.equals("f8")) {
            return B1000Types.DOUBLE;
        } else {
            throw new RuntimeException("Unknown dataType: "+dataType);
        }
    }
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        write(dos);
        dos.close();
        return out.toByteArray();
    }

    /** encodes the data as Steim1. The encoding will stop when full, the caller must check
     * the number of samples in the returned SteimFramBlock to ensure all samples were included.
     * 
     * @param recLenExp power of 2 for record size, generally 8-12
     * @return Steim1 encoding of the data
     * @throws SeedFormatException  if data is not integer or compression errors occur
     */
    public SteimFrameBlock encodeSteim1(int recLenExp) throws SeedFormatException {
        return encodeSteim1(recLenExp, 0);
    }

    /**encodes the data starting at offset as Steim1. The encoding will stop when full, the caller must check
     * the number of samples in the returned SteimFramBlock to ensure all samples were included.
     * 
     * @param recLenExp power of 2 for record size, generally 8-12
     * @param offset starting point for encoding, first sample to use
     * @return Steim1 encoding of the data
     * @throws SeedFormatException if data is not integer or compression errors occur
     */
    public SteimFrameBlock encodeSteim1(int recLenExp, int offset) throws SeedFormatException {
        try {
            if (isShortData() || isIntData()) {
                return Steim1.encode(getIntData(), (1<<recLenExp)/64 -1 , 0, offset);
            } else {
                throw new SeedFormatException("Steim1 only applicable to integer data, not float or double: "+getDataType());
            }
        } catch(SteimException e) {
            throw new SeedFormatException(e);
        }
    }

    /**
     * Coverts the data to Miniseed, spliting into multiple data records if it will not fit into one.
     * @param recLenExp power of 2 for record size, generally 8-12
     * @param doSteim1 if Steim1 compression should be applied
     * @return Miniseed records
     * @throws SeedFormatException
     */
    public List<DataRecord> toMiniSeed(int recLenExp, boolean doSteim1) throws SeedFormatException {
        if (recLenExp > 32) {
            throw new IllegalArgumentException("recLenExp should be exponent of 2, not actual size, "+recLenExp+" is too large, should be <21 and usually 8-12.");
        }
        List<DataRecord> out = new ArrayList<DataRecord>();
        int recordSize = (1 << recLenExp);
        
        if (! doSteim1) {
            // no compression so split tb to be small enough to fit in the mseed data record
            List<TraceBuf2> subList = split(recordSize-64+HEADER_SIZE);
            for (TraceBuf2 subTBuf : subList) {
                // subTBuf should fit into one data record
                out.add(subTBuf.toMiniSeedNoSplit(recLenExp, doSteim1));
            }
        } else {
            int offset = 0;
            boolean done = false;
            while (!done) {
                SteimFrameBlock sfb = encodeSteim1(recLenExp, offset); 
                sfb.trimEmptyFrames(); // remove all empties
                try {
                    out.add(toMiniSeedWithCompressedData(recLenExp, B1000Types.STEIM1, sfb.getEncodedData(), sfb.getNumSamples(), offset));
                } catch(IOException e) {
                    throw new SeedFormatException("unable to get encoded data from SteimFrameBlock", e);
                }
                offset += sfb.getNumSamples();
                if (offset == getNumSamples()) {
                    // all samples fit
                    done = true;
                }
            }
        }
        return out;
    }
    
    
    public DataRecord toMiniSeedNoSplit(int recLenExp, boolean steim1) throws SeedFormatException {
        byte[] compressedData = new byte[0];
        int compressionType = -1;
        if (steim1) {
            try {
                if (isShortData() || isIntData()) {
                    compressionType = B1000Types.STEIM1;
                    compressedData = Steim1.encode(getIntData(), (1 << recLenExp)/64 - 1).getEncodedData();
                } else {
                    throw new SeedFormatException("Steim1 only applicable to integer data, not float or double: "+getDataType());
                }
            } catch(SteimException e) {
                throw new SeedFormatException(e);
            } catch(IOException e) {
                throw new SeedFormatException(e);
            }
        } else {
            Codec codec = new Codec();
            compressionType = getSeedEncoding();
            if (isShortData()) {
                compressedData = codec.encodeAsBytes(getShortData());
            } else if (isIntData()) {
                compressedData = codec.encodeAsBytes(getIntData());
            } else if (isFloatData()) {
                compressedData = codec.encodeAsBytes(getFloatData());
            } else if (isDoubleData()) {
                compressedData = codec.encodeAsBytes(getDoubleData());
            } else {
                throw new SeedFormatException("Unknown data type: "+getDataType());
            }
        }
        return toMiniSeedWithCompressedData(recLenExp, compressionType, compressedData, getNumSamples(), 0);
        
    }
    
    /**
     * Creates a data record, copying values from the tracebuf header. 
     * @param recLenExp power of 2 for record size, generally 8-12
     * @param compressionType SEED blockette1000 compression type
     * @param compressedData compressed data
     * @param numSamples number of samples in the compressed Data
     * @param dataOffset offset into the tracebuf's data represented by the compressed data, to allow for
     *          spliting big tracebufs into mulitple miniseed records
     * @return
     * @throws SeedFormatException
     */
    DataRecord toMiniSeedWithCompressedData(int recLenExp, int compressionType, byte[] compressedData, int numSamples, int dataOffset) throws SeedFormatException {
        DataHeader dh = new DataHeader(0, 'D', false);
        String s = getStation().trim();
        if (s.length() > 5) { s = s.substring(0,5);}
        dh.setStationIdentifier(s);
        s = getChannel().trim();
        if (s.length() > 3) { s = s.substring(0,3);}
        dh.setChannelIdentifier(s);
        s = getNetwork().trim();
        if (s.length() > 2) { s = s.substring(0,2);}
        dh.setNetworkCode(s);
        s = getLocId().trim();
        if (s == null || s.equals("") || s.equals("--")) { s = "  ";}
        if (s.length() > 2) { s = s.substring(0,2);}
        dh.setLocationIdentifier(s);
        dh.setDataQualityFlags((byte)(getQuality() >> 8)); // only high byte
        dh.setNumSamples((short)numSamples);
        dh.setStartBtime(new Btime(getStartTime() + dataOffset / getSampleRate()));
        dh.setSampleRate(getSampleRate());
        DataRecord dr = new DataRecord(dh);

        Blockette1000 b1000 = new Blockette1000();
        b1000.setEncodingFormat((byte)compressionType);
        b1000.setDataRecordLength((byte)recLenExp);
        b1000.setWordOrder((byte)1); // always do big endian
        dr.addBlockette(b1000);
        
        // check we can fit it all in
        if (dh.getSize()+b1000.getSize()+compressedData.length > Math.pow(2, recLenExp)) {
            throw new DataTooLargeException("Cannot fit data into record lenght of "+recLenExp+"("+Math.pow(2, recLenExp)+"). header="+(dh.getSize()+b1000.getSize())+" data="+compressedData.length);
        }
        dr.setData(compressedData);
        return dr;
    }

    public int getSize() {
        return HEADER_SIZE+ getNumSamples()*getSampleSize(getDataType());
    }
    
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return getPin()+" "+network+"."+station+"."+locId+"."+channel+" "+
                sdf.format(getStartDate())+"("+getStartTime()+") to "+
                sdf.format(getEndDate())+"("+getEndTime()+") sr="+getSampleRate()+"  npts="+numSamples+" datetype="+getDataType()+" ver="+getVersion();
    }
    
    public String toStringWithData() {
        String out = toString()+"\n";
        if (isShortData() || isIntData()) {
            int[] d = getIntData();
            for (int i = 0; i < d.length; i++) {
                out+=d[i]+" ";
                if (i % 8 == 7) {
                    out += "\n";
                }
            }
        } else {
            double[] d = getDoubleData();
            for (int i = 0; i < d.length; i++) {
                out+=d[i]+" ";
                if (i % 8 == 7) {
                    out += "\n";
                }
            }
        }
        return out;
    }

    short[] shortData;

    int[] intData;

    float[] floatData;

    double[] doubleData;

    /* NULL string for location code field */
    public static final String LOC_NULL_STRING = "--";

    public static final int MAX_TRACEBUF_SIZE = 4096;

    /* Byte 0 of data quality flags, as in SEED format */
    public static final int AMPLIFIER_SATURATED = 0x01;

    public static final int DIGITIZER_CLIPPED = 0x02;

    public static final int SPIKES_DETECTED = 0x04;

    public static final int GLITCHES_DETECTED = 0x08;

    public static final int MISSING_DATA_PRESENT = 0x10;

    public static final int TELEMETRY_SYNCH_ERROR = 0x20;

    public static final int FILTER_CHARGING = 0x40;

    public static final int TIME_TAG_QUESTIONABLE = 0x80;

    public static final int HEADER_SIZE = 64;
    
    /* CSS datatype codes */
    public static final String SUN_IEEE_SINGLE_PRECISION_REAL = "t4";

    public static final String SUN_IEEE_DOUBLE_PRECISION_REAL = "t8";

    public static final String SUN_IEEE_INTEGER = "s4";

    public static final String SUN_IEEE_SHORT_INTEGER = "s2";

    public static final String INTEL_IEEE_SINGLE_PRECISION_REAL = "f4";

    public static final String INTEL_IEEE_DOUBLE_PRECISION_REAL = "f8";

    public static final String INTEL_IEEE_INTEGER = "i4";

    public static final String INTEL_IEEE_SHORT_INTEGER = "i2";

    public static final String NORESS_GAIN_RANGED = "g2";

    public static final String TRACEBUF_VERSION = "20";

    public static final int MAX_NET_LEN = 9;
    public static final int MAX_STA_LEN = 7;
    public static final int MAX_LOC_LEN = 3;
    public static final int MAX_CHAN_LEN = 4;
    
    
    public static final short S_ZERO = (short)0;
}
