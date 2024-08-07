package edu.sc.seis.seisFile.mseed3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FDSNSourceId {
    public static final String FDSN_PREFIX = "FDSN:";
    public static final String SEP = "_";
    public static final String sourceIdRegExString =
            "FDSN:([A-Z0-9]{1,8})"+SEP      // net
            +"([A-Z0-9-]{1,8})"+SEP         // sta
            +"([A-Z0-9-]{0,8})"+SEP          // loc
            +"([A-Z])"+SEP                  // band
            +"([A-Z0-9]+)"+SEP              // source
            +"([A-Z0-9]+)";                 // subsource
    public static final Pattern sourceIdRegEx = Pattern.compile(sourceIdRegExString);

    protected String networkCode;
    protected String stationCode;
    protected String locationCode;
    protected String bandCode;
    protected String sourceCode;
    protected String subsourceCode;

    public FDSNSourceId(String networkCode, String stationCode, String locationCode, String bandCode, String sourceCode, String subsourceCode) {
        this.networkCode = networkCode;
        this.stationCode = stationCode;
        this.locationCode = locationCode;
        this.bandCode = bandCode;
        this.sourceCode = sourceCode;
        this.subsourceCode = subsourceCode;
    }

    public static FDSNSourceId parse(String sourceIdUrl) throws FDSNSourceIdException {
        Matcher m = sourceIdRegEx.matcher(sourceIdUrl);
        if (m.matches()) {
            return new FDSNSourceId(m.group(1), m.group(2),m.group(3),m.group(4),m.group(5),m.group(6));
        } else {
            throw new FDSNSourceIdException("Parse error, does not match regular expression: "+sourceIdUrl+"  "+sourceIdRegExString);
        }
    }

    public static FDSNSourceId fromNSLC(String networkCode, String stationCode, String locationCode, String channelCode ) throws FDSNSourceIdException {
        String band = "", source = "", subsource = "";
        if (channelCode.length() == 0) {
            throw new FDSNSourceIdException("Channel code is empty");
        } else if (channelCode.length() <= 3) {
            band = channelCode.substring(0,1);
            if (channelCode.length() > 1) {
                source = channelCode.substring(1, 2);
                if (channelCode.length() > 2) {
                    subsource = channelCode.substring(2, 3);
                }
            }
        } else if (channelCode.contains("_")) {
            String[] bss = channelCode.split(FDSNSourceId.SEP, 3);
            band = bss[0];
            source = bss[1];
            if (bss.length > 2) {
                subsource = bss[2];
            }
        } else {
            throw new FDSNSourceIdException("Unable to parse channel code into band, source, subsource");
        }
        return new FDSNSourceId(networkCode.trim(),
                stationCode.trim(),
                locationCode.trim(),
                band.trim(), source.trim(), subsource.trim());
    }

    public static FDSNSourceId createUnknown(double sampleRate) throws FDSNSourceIdException {
        String sourceCode = "H";
        String networkCode = "XX";
        String stationCode = "ABC";
        String locationCode = "";
        String subsourceCode = "U";
        String bandCode = bandCodeForRate(sampleRate, 0.01);
        return new FDSNSourceId(networkCode, stationCode, locationCode, bandCode, sourceCode, subsourceCode);
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getBandCode() {
        return bandCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSubsourceCode() {
        return subsourceCode;
    }

    /**
     * If band, source and subsource are all length 1, returns a 3-char channel code like BHZ.
     * Otherwise, return separated by '_' like B_XY_Z.
     * @return combination of band, source and subsource code
     */
    public String getChannelCode() {
        if (bandCode.length()==1 && sourceCode.length()==1&&subsourceCode.length()==1) {
            return bandCode+sourceCode+subsourceCode;
        }
        return bandCode+"_"+sourceCode+"_"+subsourceCode;
    }
    @java.lang.Override
    public java.lang.String toString() {
        return "FDSN:" + networkCode + SEP +
                stationCode + SEP +
                locationCode + SEP +
                bandCode + SEP +
                sourceCode + SEP +
                subsourceCode;
    }

    /**
     * Calculates the band code for the given sample rate. Optionally taking into
     *     account the lower bound of the response, response_lb, to distinguish
     *     broadband from short period in the higher sample rates, where
     *     0.1 hertz/10 seconds is the boundary. Use of negative values implies period
     *     in seconds, while positive implies Hz.
     *
     *     See http://docs.fdsn.org/projects/source-identifiers/en/v1.0/channel-codes.html#band-code
     *
     * @param sampRate  sample rate, Hz if positive, period in seconds if negative
     * @param response_lb response lower bound in Hz if positive, period if negative
     * @return
     */
    public static String bandCodeForRate(Double sampRate, Double response_lb) throws FDSNSourceIdException {
        if (sampRate == null || sampRate == 0.0)
            return "I";
        if (sampRate < 0) {
            //assume period
            sampRate = -1/sampRate;
        }
        if (response_lb<0) {
            response_lb = -1/response_lb;
        }

        if (sampRate >= 5000) {
            return "J";
        }
        if (1000 <= sampRate && sampRate < 5000) {
            if (response_lb != null && response_lb <0.1){
                return "F";
            }
            return "G";
        }
        if (250 <= sampRate && sampRate < 1000) {
            if (response_lb != null && response_lb <0.1){
                return "C";
            }
            return "D";
        }
        if (80 <= sampRate && sampRate < 250) {
            if (response_lb != null && response_lb < 0.1) {
                return "H";
            }
            return "E";
        }
        if (10 <= sampRate && sampRate < 80) {
            if (response_lb != null && response_lb < 0.1) {
                return "B";
            }
            return "S";
        }
        if (1 < sampRate && sampRate < 10) {
            return "M";
        }
        if (0.5 < sampRate && sampRate < 1.5) {
            // spec not clear about how far from 1 is L
            return "L";
        }
        if (0.1 <= sampRate && sampRate < 1) {
            return "V";
        }
        if (0.01 <= sampRate && sampRate < 0.1) {
            return "U";
        }
        if (0.001 <= sampRate && sampRate < 0.01) {
            return "W";
        }
        if (0.0001 <= sampRate && sampRate < 0.001) {
            return "R";
        }
        if (0.00001 <= sampRate && sampRate < 0.0001) {
            return "P";
        }
        if (0.000001 <= sampRate && sampRate < 0.00001) {
            return "T";
        }
        if (sampRate < 0.000001) {
            return "Q";
        }
        // this should never happen
        throw new FDSNSourceIdException(
                "Unable to calc band code for: "+sampRate+" "+response_lb);
    }
}