package edu.sc.seis.seisFile.mseed3;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FDSNStationSourceId {

    public static final String sourceIdRegExString =
            "FDSN:([A-Z0-9]{1,8})"+FDSNSourceId.SEP      // net
                    +"([A-Z0-9-]{1,8})";                 // subsource
    public static final Pattern sourceIdRegEx = Pattern.compile(sourceIdRegExString);

    protected String networkCode;
    protected String stationCode;

    public FDSNStationSourceId(String networkCode, String stationCode) {
        this.networkCode = networkCode;
        this.stationCode = stationCode;
    }

    public static FDSNStationSourceId parse(String sourceIdUrl) throws FDSNSourceIdException {
        Matcher m = sourceIdRegEx.matcher(sourceIdUrl);
        if (m.matches()) {
            return new FDSNStationSourceId(m.group(1), m.group(2));
        } else {
            throw new FDSNSourceIdException("Parse error, does not match regular expression: "+sourceIdUrl+"  "+sourceIdRegExString);
        }
    }

    public static FDSNStationSourceId createUnknown() throws FDSNSourceIdException {
        return new FDSNStationSourceId(FDSNSourceId.DEFAULT_NETWORK_CODE, FDSNSourceId.DEFAULT_STATION_CODE);
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getStationCode() {
        return stationCode;
    }


    @java.lang.Override
    public java.lang.String toString() {
        return "FDSN:" + networkCode + FDSNSourceId.SEP +
                stationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FDSNSourceId)) return false;
        FDSNSourceId that = (FDSNSourceId) o;
        return Objects.equals(networkCode, that.networkCode)
                && Objects.equals(stationCode, that.stationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkCode, stationCode);
    }
}
