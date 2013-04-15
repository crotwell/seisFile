package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.net.URI;
import java.net.URISyntaxException;

public class FDSNQuakeMLQueryParams {

    public FDSNQuakeMLQueryParams() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Limit to events on or after the specified start time .
     */
    public static final String starttime = "starttime";

    /**
     * Limit to events on or before the specified end time .
     */
    public static final String endtime = "endtime";

    /**
     * Limit to events with a latitude larger than the specified minimum.
     */
    public static final String minlatitude = "minlatitude";

    /**
     * Limit to events with a latitude smaller than the specified maximum.
     */
    public static final String maxlatitude = "maxlatitude";

    /**
     * Limit to events with a longitude larger than the specified minimum.
     */
    public static final String minlongitude = "minlongitude";

    public static final String maxlongitude = "maxlongitude";

    /**
     * Limit to events with a longitude smaller than the specified maximum.
     */
    /**
     * Specify the latitude to be used for a radius search.
     */
    public static final String latitude = "latitude";

    /** Specify the longitude to the used for a radius search. */
    public static final String longitude = "longitude";

    /**
     * Limit to events within the specified minimum number of degrees from the
     * geographic point defined by the latitude and longitude parameters.
     */
    public static final String minradius = "minradius";

    /**
     * Lim it to events within the specified maximum number of degrees from the
     * geographic point defined by the latitude and longitude parameters.
     */
    public static final String maxradius = "maxradius";

    /**
     * Limit to events with depth more than the specified minimum.
     */
    public static final String mindepth = "mindepth";

    /**
     * Limit to events with depth less than the specified maximum .
     */
    public static final String maxdepth = "maxdepth";

    /**
     * Limit to events with a magnitude larger than the specified minimum.
     */
    public static final String minmagnitude = "minmagnitude";

    /** Limit to events with a magnitude smaller than the specified maximum. */
    public static final String maxmagnitude = "maxmagnitude";

    /**
     * Specify a magnitude type to use for testing the minimum and maximum
     * limits.
     */
    public static final String magnitudetype = "magnitudetype";

    /**
     * Specify if all origins for the event should be included, default is data
     * center dependent but is suggested to be the preferred origin only.
     */
    public static final String includeallorigins = "includeallorigins";

    /**
     * Specify if all magnitudes for the event should be included, default is
     * data center dependent but is suggested to be the preferred magnitude
     * only.
     */
    public static final String includeallmagnitudes = "includeallmagnitudes";

    /** Specify if phase arrivals should be included. */
    public static final String includearrivals = "includearrivals";

    /**
     * Select a specific event by ID; event identifier s are data center
     * specific.
     */
    public static final String eventid = "eventid";

    /** Limit the results to the specified number of events. */
    public static final String limit = "limit";

    /**
     * Return results starting at the event count specified , starting at 1 .
     */
    public static final String offset = "offset";

    /**
     * Order the result by time or magnitude with the following possibilities:
     * time : order by origin descending time time - asc : order by origin
     * ascending time magnitude : order by descending magnitude magnitude - asc
     * : order by ascending magnitude
     */
    public static final String orderby = "orderby";

    /**
     * Limit to events from a specified catalog
     */
    public static final String catalog = "catalog";

    /**
     * Limit to events contributed by a specified contributor .
     */
    public static final String contributor = "contributor";

    /** Limit to events updated after the specified time. */
    public static final String updatedafter = "updatedafter";

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/event/1/query?";
    
    public static final URI IRIS_BASE_URI;
    
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string"+IRIS_BASE_URL);
        }
    }
}
