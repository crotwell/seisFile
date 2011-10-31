package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

    public Blockette200(byte[] info, boolean swapBytes) throws SeedFormatException {
        super(info, swapBytes);
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
        return Float.intBitsToFloat(Utility.bytesToInt(info, SIGNAL, swapBytes));
    }

    /**
     * @return - the signal period field
     */
    public float getPeriod() {
        return Float.intBitsToFloat(Utility.bytesToInt(info, PERIOD, swapBytes));
    }

    /**
     * @return - the background estimate field
     */
    public float getBackground() {
        return Float.intBitsToFloat(Utility.bytesToInt(info, BACKGROUND, swapBytes));
    }

    /**
     * @return - the signal onset time field
     */
    public Btime getSignalOnset() {
        return new Btime(info, SIGNAL_ONSET);
    }
    
    public String getEventDetector(){
        return new String(info, EVENT_DETECTOR, EVENT_DETECTOR_LENGTH);
    }

    public void writeASCII(PrintWriter out) throws IOException {
        out.println("Blockette200 sig="+getSignal()+" per="+getPeriod()+" bkgrd="+getBackground());
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o instanceof Blockette200) {
            byte[] oinfo = ((Blockette200)o).info;
            if(info.length != oinfo.length){
                return false;
            }
            for(int i = 0; i < oinfo.length; i++) {
                if(info[i] != oinfo[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
