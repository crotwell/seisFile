package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.net.URI;
import java.net.URISyntaxException;

import edu.sc.seis.seisFile.fdsnws.AbstractQueryParams;

public class FDSNStationXMLQueryParams extends AbstractQueryParams {

    public FDSNStationXMLQueryParams() {
        this(IRIS_BASE_URI);
    }
    
    public FDSNStationXMLQueryParams(URI baseUri) {
        super(baseUri);
    }

    /**
     * Limit to metadata epochs starting on or after the specified start time .
     */
    public static final String STARTTIME = "starttime";

    /**
     * Limit to metadata epochs ending on or before the specified end time .
     */
    public static final String ENDTIME = "endtime";

    /**
     * Limit to metadata epochs starting before specified time .
     */
    public static final String STARTBEFORE = "startbefore";

    /**
     * Limit to metadata epochs starting after specified time .
     */
    public static final String STARTAFTER = "startafter";

    /**
     * Limit to metadata epochs ending before specified time .
     */
    public static final String ENDBEFORE = "endbefore";

    /**
     * Limit to metadata epochs ending after specified time .
     */
    public static final String ENDAFTER = "endafter";

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
     * Limit to stations with a latitude larger than the specified minimum.
     */
    public static final String MINLATITUDE = "minlatitude";

    /** Limit to stations with a latitude smaller than the specified maximum. */
    public static final String MAXLATITUDE = "maxlatitude";

    /** Limit to stations with a longitude larger than the specified minimum. */
    public static final String MINLONGITUDE = "minlongitude";

    /**
     * Limit to stations with a longitude smaller than the spe cified maximum.
     */
    public static final String MAXLONGITUDE = "maxlongitude";

    /**
     * Specify the latitude to be used for a radius search .
     */
    public static final String LATITUDE = "latitude";

    /**
     * Specify the longitude to the used for a radius search .
     */
    public static final String LONGITUDE = "longitude";

    /**
     * Limit results to stations within the specified minimum number of degrees
     * from the geographic point defined by the latitude and longitude
     * parameters.
     */
    public static final String MINRADIUS = "minradius";

    /**
     * Limit results to stations within the specified maximum number of degrees
     * from the geographic point defined by the latitude and longitude
     * parameters.
     */
    public static final String MAXRADIUS = "maxradius";

    /**
     * Specify the level of detail for the results .
     */
    public static final String LEVEL = "level";

    /** Specify if results should include information for restricted stations. */
    public static final String INCLUDERESTRICTED = "includerestricted";

    /**
     * Specify if results should include information about time series data
     * availability.
     */
    public static final String INCLUDEAVAILABILITY = "includeavailability";

    /**
     * Limit to metadata updated after specified date; updates are data center
     * specific.
     */
    public static final String UPDATEDAFTER = "updatedafter";

    public static final String NETWORK_LEVEL = "network";

    public static final String STATION_LEVEL = "station";

    public static final String CHANNEL_LEVEL = "channel";

    public static final String RESPONSE_LEVEL = "response";

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/station/1/query?";
    
    public static final URI IRIS_BASE_URI;
    
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string"+IRIS_BASE_URL);
        }
    }
}
