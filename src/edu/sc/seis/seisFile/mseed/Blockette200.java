package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Blockette200 extends DataBlockette {

    public Blockette200(float signal,
                        float period,
                        float background,
                        Btime signalOnset,
                        String eventDetector) {
        super(B200_SIZE);
        Utility.insertFloat(signal, info, SIGNAL);
        Utility.insertFloat(period, info, PERIOD);
        Utility.insertFloat(background, info, BACKGROUND);
        byte[] onsetBytes = signalOnset.getAsBytes();
        System.arraycopy(onsetBytes, 0, info, SIGNAL_ONSET, onsetBytes.length);
        if(eventDetector.length() > EVENT_DETECTOR_LENGTH) {
            throw new IllegalArgumentException("The event detector can only be up to "
                    + EVENT_DETECTOR_LENGTH + " characters in length");
        }
        byte[] detectorBytes;
        try {
            detectorBytes = eventDetector.getBytes("US-ASCII");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException("Java was unable to find the US-ASCII character encoding.");
        }
        if(detectorBytes.length != eventDetector.length()) {
            throw new IllegalArgumentException("The characters in event detector must be in the ASCII character set i.e. from 0-127");
        }
        detectorBytes = Utility.pad(detectorBytes,
                                    EVENT_DETECTOR_LENGTH,
                                    (byte)' ');
        System.arraycopy(detectorBytes,
                         0,
                         info,
                         EVENT_DETECTOR,
                         detectorBytes.length);
    }

    public Blockette200(byte[] info) {
        super(info);
        trimToSize(getSize());
    }

    public String getName() {
        return "Generic Event Detection Blockette";
    }

    public int getSize() {
        return B200_SIZE;
    }

    public int getType() {
        return 200;
    }

    /**
     * @return - the signal amplitude field
     */
    public float getSignal() {
        return Float.intBitsToFloat(Utility.bytesToInt(info, SIGNAL));
    }

    /**
     * @return - the signal period field
     */
    public float getPeriod() {
        return Float.intBitsToFloat(Utility.bytesToInt(info, PERIOD));
    }

    /**
     * @return - the background estimate field
     */
    public float getBackground() {
        return Float.intBitsToFloat(Utility.bytesToInt(info, BACKGROUND));
    }

    /**
     * @return - the signal onset time field
     */
    public Btime getSignalOnset() {
        return new Btime(info, SIGNAL_ONSET);
    }

    public void writeASCII(Writer out) throws IOException {
        out.write("Blockette200");
    }

    // Offsets for various fields
    private static final int SIGNAL = 4;

    private static final int PERIOD = 8;

    private static final int BACKGROUND = 12;

    private static final int SIGNAL_ONSET = 18;

    private static final int EVENT_DETECTOR = 28;

    // Full size of blockette 200
    private static final int B200_SIZE = 52;

    private static final int EVENT_DETECTOR_LENGTH = 24;
}
