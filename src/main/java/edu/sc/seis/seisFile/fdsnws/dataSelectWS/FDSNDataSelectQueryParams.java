package edu.sc.seis.seisFile.fdsnws.dataSelectWS;

import java.net.URI;
import java.net.URISyntaxException;

import edu.sc.seis.seisFile.fdsnws.AbstractQueryParams;

public class FDSNDataSelectQueryParams extends AbstractQueryParams {

    public FDSNDataSelectQueryParams() {
        this(IRIS_BASE_URI);
    }

    public FDSNDataSelectQueryParams(URI baseUri) {
        super(baseUri);
    }
    
    /**
     * Limit results to time series samples on or after the specified start time
     */
    public static final String STARTTIME = "starttime";

    /** Limit results to time series samples on or before the specified end time */
    public static final String ENDTIME = "endtime";

    /**
     * Select one or more network codes. Can be SEED network codes or data
     * center defined codes. Multiple codes are comma - separated.
     */
    public static final String NETWORK = "network";

    /**
     * Select one or more SEED station codes. Multiple codes are comma -
     * separated.
     */
    public static final String STATION = "station";

    /**
     * Select one or more SEED location identifiers. Multiple identifiers are
     * comma - separated. As a special case Ò -- Ò (two dashes) will be
     * translated to a string of two space characters to match blank location
     * IDs.
     */
    public static final String LOCATION = "location";

    /**
     * Select one or more SEED channel codes. Multiple codes are comma -
     * separated.
     */
    public static final String CHANNEL = "channel";

    /**
     * Select a specific SEED quality indicato r, handling is data center
     * dependent.
     */
    public static final String QUALITY = "quality";

    /**
     * Limit results to continuous data segments of a minimum length specified
     * in seconds,
     */
    public static final String MINIMUMLENGTH = "minimumlength";

    /** Limit results to the longest continuous segment per channel. */
    public static final String LONGESTONLY = "longestonly";
    

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/dataselect/1/query?";
    
    public static final URI IRIS_BASE_URI;
    
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string"+IRIS_BASE_URL);
        }
    }
}
