package edu.sc.seis.seisFile.fdsnws;

import java.util.Date;

public class IRISWSVirtualNetworkQueryParams extends AbstractQueryParams  {

    public IRISWSVirtualNetworkQueryParams() {
        super(IRIS_HOST);
    }
    
    public IRISWSVirtualNetworkQueryParams(String host) {
        super(host==null ? IRIS_HOST : host);
    }

    @Override
    public String getServiceName() {
        // TODO Auto-generated method stub
        return null;
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
    public IRISWSVirtualNetworkQueryParams setStartTime(Date value) {
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
    public IRISWSVirtualNetworkQueryParams setEndTime(Date value) {
        setParam(ENDTIME, value);
        return this;
    }

    public IRISWSVirtualNetworkQueryParams clearEndTime() {
        clearParam(ENDTIME);
        return this;
    }

}
