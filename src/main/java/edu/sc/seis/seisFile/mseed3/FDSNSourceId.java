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

    public static FDSNSourceId fromNSLC(String networkCode, String stationCode, String locationCode, String channelCode ) {
        String band, source, subsource;
        if (channelCode.length() == 3) {
            band = channelCode.substring(0,1);
            source = channelCode.substring(1,2);
            subsource = channelCode.substring(2,3);
        } else {
            String[] bss = channelCode.split(FDSNSourceId.SEP);
            band = bss[0];
            source = bss[1];
            subsource = bss[2];
        }
        return new FDSNSourceId(networkCode.trim(),
                stationCode.trim(),
                locationCode.trim(),
                band, source, subsource);
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
}