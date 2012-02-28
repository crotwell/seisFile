package edu.sc.seis.seisFile.winston;

import edu.sc.seis.seisFile.mseed.Utility;

public class TraceBuf2 {

    public TraceBuf2(byte[] data) {
        dataType = Utility.extractString(data, 57, 3);
        boolean swapBytes = false;
        if (dataType.equals(INTEL_IEEE_DOUBLE_PRECISION_REAL) || dataType.equals(INTEL_IEEE_INTEGER)
                || dataType.equals(INTEL_IEEE_SHORT_INTEGER) || dataType.equals(INTEL_IEEE_SINGLE_PRECISION_REAL)) {
            swapBytes = true;
        }
        pin = Utility.bytesToInt(data, 0, swapBytes);
        numSamples = Utility.bytesToInt(data, 4, swapBytes);
        startTime = Double.longBitsToDouble(Utility.bytesToLong(data, 8, swapBytes));
        endTime = Double.longBitsToDouble(Utility.bytesToLong(data, 16, swapBytes));
        sampleRate = Double.longBitsToDouble(Utility.bytesToLong(data, 24, swapBytes));
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
