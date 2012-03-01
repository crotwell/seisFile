package edu.sc.seis.seisFile.winston;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import edu.iris.dmc.seedcodec.Codec;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.Utility;

public class TraceBuf2 {

    public TraceBuf2(byte[] data) {
        dataType = Utility.extractNullTermString(data, 57, 3);
        boolean swapBytes = false;
        if (dataType.equals(INTEL_IEEE_DOUBLE_PRECISION_REAL) || dataType.equals(INTEL_IEEE_INTEGER)
                || dataType.equals(INTEL_IEEE_SHORT_INTEGER) || dataType.equals(INTEL_IEEE_SINGLE_PRECISION_REAL)) {
            swapBytes = true;
        }
        pin = Utility.bytesToInt(data, 0, swapBytes);
        numSamples = Utility.bytesToInt(data, 4, swapBytes);
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
        return dataType.equals(INTEL_IEEE_SHORT_INTEGER) || dataType.equals(SUN_IEEE_SHORT_INTEGER);
    }

    public boolean isIntData() {
        return dataType.equals(INTEL_IEEE_INTEGER) || dataType.equals(SUN_IEEE_INTEGER);
    }

    public boolean isFloatData() {
        return dataType.equals(INTEL_IEEE_SINGLE_PRECISION_REAL) || dataType.equals(SUN_IEEE_SINGLE_PRECISION_REAL);
    }

    public boolean isDoubleData() {
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
        return intData;
    }

    public float[] getFloatData() {
        return floatData;
    }

    public double[] getDoubleData() {
        return doubleData;
    }
    
    public byte getSeedEncoding() {
        if (dataType.equals("s4") || dataType.equals("i4")) {
            return 3;
        } else if (dataType.equals("s2") || dataType.equals("i2")) {
            return 1;
        } else if (dataType.equals("t4") || dataType.equals("f4")) {
            return 4;
        } else if (dataType.equals("t8") || dataType.equals("f8")) {
            return 5;
        } else {
            throw new RuntimeException("Unknown dataType: "+dataType);
        }
    }
    
    public DataRecord toMiniSeed() throws SeedFormatException {
        DataHeader dh = new DataHeader(0, 'D', false);
        dh.setStationIdentifier(getStation());
        dh.setChannelIdentifier(getChannel());
        dh.setNetworkCode(getNetwork());
        dh.setLocationIdentifier(getLocId());
        dh.setDataQualityFlags((byte)getQuality().charAt(0));
        dh.setNumSamples((short)getNumSamples());
        dh.setStartBtime(new Btime(getStartDate()));
        dh.setSampleRate(getSampleRate());
        DataRecord dr = new DataRecord(dh);
        Blockette1000 b1000 = new Blockette1000();
        b1000.setEncodingFormat(getSeedEncoding());
        b1000.setDataRecordLength((byte)12); // 12 => 4096
        b1000.setWordOrder((byte)1); // always do big endian
        dr.addBlockette(b1000);
        Codec codec = new Codec();
        byte[] dataBytes = new byte[0];
        if (isShortData()) {
            dataBytes = codec.encodeAsBytes(getShortData());
        } else if (isIntData()) {
            dataBytes = codec.encodeAsBytes(getIntData());
        } else if (isFloatData()) {
            dataBytes = codec.encodeAsBytes(getFloatData());
        } else if (isDoubleData()) {
            dataBytes = codec.encodeAsBytes(getDoubleData());
        }
        dr.setData(dataBytes);
        return dr;
    }
    
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return getPin()+" "+network+"."+station+"."+locId+"."+channel+" "+
                sdf.format(getStartDate())+"("+getStartTime()+") to "+
                sdf.format(getEndDate())+"("+getEndTime()+") sr="+getSampleRate()+"  npts="+numSamples+" datetype="+getDataType()+" ver="+getVersion();
    }

    short[] shortData;

    int[] intData;

    float[] floatData;

    double[] doubleData;

    /* NULL string for location code field */
    public static final String LOC_NULL_STRING = "--";

    public static final int MAX_TRACEBUF_SIZ = 4096;

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
