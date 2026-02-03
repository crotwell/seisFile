package edu.sc.seis.seisFile.fdsnws;

import java.util.HashMap;

import static edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQueryParams.IRISWS_PATH;

public class IRISFedCatQueryParams extends AbstractQueryParams {

    public static final String  TARGET_DATASELECT = "dataselect";
    public static final String  TARGET_STATION = "station";

    public IRISFedCatQueryParams(FDSNStationQueryParams stationQueryParams) {
        this(DEFAULT_HOST, stationQueryParams);
    }

    public IRISFedCatQueryParams(FDSNDataSelectQueryParams dataSelectQueryParams) {
        this(DEFAULT_HOST, dataSelectQueryParams);
    }

    public IRISFedCatQueryParams(String host,
                                 FDSNStationQueryParams stationQueryParams) {
        super(host==null ? DEFAULT_HOST : host);
        setFdsnwsPath(IRISWS_PATH);
        this.stationQueryParams = stationQueryParams;
    }

    public IRISFedCatQueryParams(String host,
                                 FDSNDataSelectQueryParams dataSelectQueryParams) {
        super(host==null ? DEFAULT_HOST : host);
        setFdsnwsPath(IRISWS_PATH);
        this.dataSelectQueryParams = dataSelectQueryParams;
    }

    public static final String FORMAT_TEXT = "text";
    public static final String FORMAT_REQUEST = "request";

    public IRISFedCatQueryParams clearFormat() {
        clearParam(FORMAT);
        return this;
    }

    /** Specify the format for the results.
     *  @param value value to set
     *  @return the queryParams for method chaining
     */
    public IRISFedCatQueryParams setFormat(String value) {
        setParam(FORMAT, value);
        return this;
    }
    public static final String TARGET_SERVICE = "targetservice";

    public IRISFedCatQueryParams clearTargetService() {
        clearParam(TARGET_SERVICE);
        return this;
    }

    /** Specify the target service for the results.
     *  Either station or dataselect.
     *  @param value value to set
     *  @return the queryParams for method chaining
     */
    public IRISFedCatQueryParams setTargetService(String value) {
        setParam(TARGET_SERVICE, value);
        return this;
    }

    /**
     * Service name as defined by the fdsn, ie event, station or dataselect.
     */
    @Override
    public String getServiceName() {
        return IRISFedCatQuerier.SERVICE_NAME;
    }

    public boolean isStation() {
        return stationQueryParams != null;
    }
    public boolean isDataSelect() {
        return dataSelectQueryParams != null;
    }

    public HashMap<String, String> getParams() {
        HashMap<String, String> out = new HashMap<>();
        out.putAll(params);
        if (isStation()) {
            out.putAll(stationQueryParams.getParams());
            out.put(TARGET_SERVICE, TARGET_STATION);
        }
        if (isDataSelect()) {
            out.putAll(dataSelectQueryParams.getParams());
            out.put(TARGET_SERVICE, TARGET_DATASELECT);
        }
        return out;
    }

    FDSNStationQueryParams stationQueryParams = null;
    FDSNDataSelectQueryParams dataSelectQueryParams = null;
}
