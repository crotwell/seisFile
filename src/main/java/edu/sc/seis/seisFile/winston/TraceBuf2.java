package edu.sc.seis.seisFile.winston;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.Steim1;
import edu.iris.dmc.seedcodec.SteimException;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
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
                     String quality,
                     String pad) {
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
        this.version = version;
        this.dataType = dataType;
        this.quality = quality;
        this.pad = pad;
    }

    public TraceBuf2(int pin,
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
                     String quality,
                     String pad,
                     int[] intData) {
        this(pin, numSamples, startTime, endTime, sampleRate, station, network, channel, locId, version, dataType, quality, pad);
        this.intData = intData;
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
        version = Utility.extractString(data, 55, 2);
        dataType = Utility.extractNullTermString(data, 57, 3);
        quality = Utility.extractString(data, 60, 2);
        pad = Utility.extractString(data, 62, 2);
        int offset = 64;
        
        if (isShortData()) {
            shortData = new short[numSamples];
            for (int i = 0; i < shortData.length; i++) {
                shortData[i] = Utility.bytesToShort(data[offset], data[offset + 1], swapBytes);
                offset += 2;
            }
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

        int p = 7 - station.length();
        out.writeBytes(station);
        for (int i = 0; i < p; i++)
            out.write((byte)0);

        p = 9 - network.length();
        out.writeBytes(network);
        for (int i = 0; i < p; i++)
            out.write((byte)0);

        p = 4 - channel.length();
        out.writeBytes(channel);
        for (int i = 0; i < p; i++)
            out.write((byte)0);

        p = 5 - locId.length();
        out.writeBytes(locId);
        for (int i = 0; i < p; i++)
            out.write((byte)0);

        out.writeBytes(dataType);
        out.write((byte)0);
        out.writeBytes(quality);
        for (int i = quality.length(); i < 2; i++) {
            out.write((byte)0);
        }
        out.writeBytes(pad);
        for (int i = pad.length(); i < 2; i++) {
            out.write((byte)0);
        }

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
        out.write((byte)0);
    }
    
    public List<TraceBuf2> split(int maxPoints) {
        List<TraceBuf2> out = new ArrayList<TraceBuf2>();
        if (numSamples <= maxPoints) {
            out.add(this);
        } else {
            int curSample = 0;
            while(curSample < numSamples) {
                int splitPoints = Math.min(maxPoints, (numSamples+1)/2);
                double splitEndTime = startTime+splitPoints*sampleRate;
                TraceBuf2 first = new TraceBuf2( pin,
                                                 splitPoints,
                                                 startTime,
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
    String quality;

    /** padding */
    String pad;

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

    public String getQuality() {
        return quality;
    }

    public String getPad() {
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

    /** default miniseed of len 12 (=> 4096) and no compression.
     *  
     * @return
     * @throws SeedFormatException
     */
    public DataRecord toMiniSeed() throws SeedFormatException {
        return toMiniSeed(12, false);
    }

    public DataRecord toMiniSeed(int recLen, boolean steim1) throws SeedFormatException {
        DataHeader dh = new DataHeader(0, 'D', false);
        dh.setStationIdentifier(getStation());
        dh.setChannelIdentifier(getChannel());
        dh.setNetworkCode(getNetwork());
        String l = getLocId();
        if (l == null || l.equals("--")) { l = "  ";}
        dh.setLocationIdentifier(l);
        dh.setDataQualityFlags((byte)getQuality().charAt(0));
        dh.setNumSamples((short)getNumSamples());
        dh.setStartBtime(new Btime(getStartDate()));
        dh.setSampleRate(getSampleRate());
        DataRecord dr = new DataRecord(dh);
        Blockette1000 b1000 = new Blockette1000();
        if (steim1) {
            b1000.setEncodingFormat((byte)B1000Types.STEIM1);
        } else {
            b1000.setEncodingFormat(getSeedEncoding());
        }
        b1000.setDataRecordLength((byte)recLen);
        b1000.setWordOrder((byte)1); // always do big endian
        dr.addBlockette(b1000);
        Codec codec = new Codec();
        byte[] dataBytes = new byte[0];
        if (steim1) {
            try {
                if (isShortData() || isIntData()) {
                    dataBytes = Steim1.encode(getIntData(), (1 << recLen)/64 - 1).getEncodedData();
                } else {
                    throw new SeedFormatException("Steim1 only applicable to integer data, not float or double: "+getDataType());
                }
            } catch(SteimException e) {
                throw new SeedFormatException(e);
            } catch(IOException e) {
                throw new SeedFormatException(e);
            }
        } else {
            if (isShortData()) {
                dataBytes = codec.encodeAsBytes(getShortData());
            } else if (isIntData()) {
                dataBytes = codec.encodeAsBytes(getIntData());
            } else if (isFloatData()) {
                dataBytes = codec.encodeAsBytes(getFloatData());
            } else if (isDoubleData()) {
                dataBytes = codec.encodeAsBytes(getDoubleData());
            }
        }
        // check we can fit it all in
        if (dh.getSize()+b1000.getSize()+dataBytes.length > Math.pow(2, recLen)) {
            throw new SeedFormatException("Cannot fit data into record lenght of "+recLen+"("+Math.pow(2, recLen)+"). header="+(dh.getSize()+b1000.getSize())+" data="+dataBytes.length);
        }
        dr.setData(dataBytes);
        return dr;
    }

    public int getSize() {
        return 64+ getNumSamples()*getSampleSize(getDataType());
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

}
