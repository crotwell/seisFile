package edu.sc.seis.seisFile.fdsnws;

import java.time.Instant;

public class IRISWSVirtualNetworkQueryParams extends AbstractQueryParams  {

    public IRISWSVirtualNetworkQueryParams() {
        this(IRIS_HOST);
    }
    
    public IRISWSVirtualNetworkQueryParams(String host) {
        super(host==null ? IRIS_HOST : host);
        setFdsnwsPath(IRISWS_PATH);
    }

    @Override
    public String getServiceName() {
        return VIRTUALNETWORK_SERVICE;
    }

    public static final String VIRTUALNETWORK_SERVICE = "virtualnetwork";



    public IRISWSVirtualNetworkQueryParams setHost(String host) {
        this.host = host;
        return this;
    }
    public IRISWSVirtualNetworkQueryParams setPort(int port) {
        this.port = port;
        return this;
    }

    public static final String CODE = "code";

    /** Specify a magnitude type to use for testing the minimum and maximum limits.
     */
    public IRISWSVirtualNetworkQueryParams setCode(String value) {
        setParam(CODE, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearCode() {
        clearParam(CODE);
        return this;
    }

    public static final String FORMAT = "format";

    /** Specify a magnitude type to use for testing the minimum and maximum limits.
     */
    public IRISWSVirtualNetworkQueryParams setFormat(String value) {
        setParam(FORMAT, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearFormat() {
        clearParam(FORMAT);
        return this;
    }

    public static final String DEFINITION = "definition";

    /** Specify a magnitude type to use for testing the minimum and maximum limits.
     */
    public IRISWSVirtualNetworkQueryParams setDefinition(boolean value) {
        setParam(DEFINITION, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearDefinition() {
        clearParam(DEFINITION);
        return this;
    }


    public static final String STARTTIME = "starttime";

    /** Limit to events on or after the specified start time.
     */
    public IRISWSVirtualNetworkQueryParams setStartTime(Instant value) {
        setParam(STARTTIME, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearStartTime() {
        clearParam(STARTTIME);
        return this;
    }


    public static final String ENDTIME = "endtime";

    /** Limit to events on or before the specified end time.
     */
    public IRISWSVirtualNetworkQueryParams setEndTime(Instant value) {
        setParam(ENDTIME, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearEndTime() {
        clearParam(ENDTIME);
        return this;
    }

    public static final String IRISWS_PATH = "irisws";
}
