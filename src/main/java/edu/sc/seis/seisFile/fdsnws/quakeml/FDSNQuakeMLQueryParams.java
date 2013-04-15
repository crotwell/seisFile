package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.net.URI;
import java.net.URISyntaxException;

import edu.sc.seis.seisFile.fdsnws.AbstractQueryParams;

public class FDSNQuakeMLQueryParams extends AbstractQueryParams{

    public FDSNQuakeMLQueryParams() {
        this(IRIS_BASE_URI);
    }
    
    public FDSNQuakeMLQueryParams(URI baseUri) {
        super(baseUri);
    }

    /**
     * Limit to events on or after the specified start time .
     */
    public static final String STARTTIME = "starttime";

    /**
     * Limit to events on or before the specified end time .
     */
    public static final String ENDTIME = "endtime";

    /**
     * Limit to events with a latitude larger than the specified minimum.
     */
    public static final String MINLATITUDE = "minlatitude";

    /**
     * Limit to events with a latitude smaller than the specified maximum.
     */
    public static final String MAXLATITUDE = "maxlatitude";

    /**
     * Limit to events with a longitude larger than the specified minimum.
     */
    public static final String MINLONGITUDE = "minlongitude";

    /**
     * Limit to events with a longitude smaller than the specified maximum.
     */
    public static final String MAXLONGITUDE = "maxlongitude";

    /**
     * Specify the latitude to be used for a radius search.
     */
    public static final String LATITUDE = "latitude";

    /** Specify the longitude to the used for a radius search. */
    public static final String LONGITUDE = "longitude";

    /**
     * Limit to events within the specified minimum number of degrees from the
     * geographic point defined by the latitude and longitude parameters.
     */
    public static final String MINRADIUS = "minradius";

    /**
     * Lim it to events within the specified maximum number of degrees from the
     * geographic point defined by the latitude and longitude parameters.
     */
    public static final String MAXRADIUS = "maxradius";

    /**
     * Limit to events with depth more than the specified minimum.
     */
    public static final String MINDEPTH = "mindepth";

    /**
     * Limit to events with depth less than the specified maximum .
     */
    public static final String MAXDEPTH = "maxdepth";

    /**
     * Limit to events with a magnitude larger than the specified minimum.
     */
    public static final String MINMAGNITUDE = "minmagnitude";

    /** Limit to events with a magnitude smaller than the specified maximum. */
    public static final String MAXMAGNITUDE = "maxmagnitude";

    /**
     * Specify a magnitude type to use for testing the minimum and maximum
     * limits.
     */
    public static final String MAGNITUDETYPE = "magnitudetype";

    /**
     * Specify if all origins for the event should be included, default is data
     * center dependent but is suggested to be the preferred origin only.
     */
    public static final String INCLUDEALLORIGINS = "includeallorigins";

    /**
     * Specify if all magnitudes for the event should be included, default is
     * data center dependent but is suggested to be the preferred magnitude
     * only.
     */
    public static final String INCLUDEALLMAGNITUDES = "includeallmagnitudes";

    /** Specify if phase arrivals should be included. */
    public static final String INCLUDEARRIVALS = "includearrivals";

    /**
     * Select a specific event by ID; event identifier s are data center
     * specific.
     */
    public static final String EVENTID = "eventid";

    /** Limit the results to the specified number of events. */
    public static final String LIMIT = "limit";

    /**
     * Return results starting at the event count specified , starting at 1 .
     */
    public static final String OFFSET = "offset";

    /**
     * Order the result by time or magnitude with the following possibilities:<br/>
     * time : order by origin descending time <br/>
     * time - asc : order by origin ascending time <br/>
     * magnitude : order by descending magnitude <br/>
     * magnitude - asc : order by ascending magnitude
     */
    public static final String ORDERBY = "orderby";

    /**
     * Limit to events from a specified catalog
     */
    public static final String CATALOG = "catalog";

    /**
     * Limit to events contributed by a specified contributor .
     */
    public static final String CONTRIBUTOR = "contributor";

    /** Limit to events updated after the specified time. */
    public static final String UPDATEDAFTER = "updatedafter";

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/event/1/query?";

    public static final URI IRIS_BASE_URI;
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch(URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string" + IRIS_BASE_URL);
        }
    }
}
