
package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSNEventQueryParams extends AbstractQueryParams {

    public FDSNEventQueryParams() {
        this(IRIS_BASE_URI);
    }
    
    public FDSNEventQueryParams(URI baseUri) {
        super(baseUri);
    }


    public static final String STARTTIME = "starttime";

    /** Limit to events on or after the specified start time.
     */
    public FDSNEventQueryParams setStartTime(Date value) {
        setParam(STARTTIME, value);
        return this;
    }

    public FDSNEventQueryParams clearStartTime() {
        clearParam(STARTTIME);
        return this;
    }


    public static final String ENDTIME = "endtime";

    /** Limit to events on or before the specified end time.
     */
    public FDSNEventQueryParams setEndTime(Date value) {
        setParam(ENDTIME, value);
        return this;
    }

    public FDSNEventQueryParams clearEndTime() {
        clearParam(ENDTIME);
        return this;
    }


    public static final String MINLATITUDE = "minlatitude";

    /** Limit to events with a latitude larger than the specified minimum.
     */
    public FDSNEventQueryParams setMinLatitude(float value) {
        setParam(MINLATITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMinLatitude() {
        clearParam(MINLATITUDE);
        return this;
    }


    public static final String MAXLATITUDE = "maxlatitude";

    /** Limit to events with a latitude smaller than the specified maximum.
     */
    public FDSNEventQueryParams setMaxLatitude(float value) {
        setParam(MAXLATITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMaxLatitude() {
        clearParam(MAXLATITUDE);
        return this;
    }


    public static final String MINLONGITUDE = "minlongitude";

    /** Limit to events with a longitude larger than the specified minimum.
     */
    public FDSNEventQueryParams setMinLongitude(float value) {
        setParam(MINLONGITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMinLongitude() {
        clearParam(MINLONGITUDE);
        return this;
    }


    public static final String MAXLONGITUDE = "maxlongitude";

    /** Limit to events with a longitude smaller than the specified maximum.
     */
    public FDSNEventQueryParams setMaxLongitude(float value) {
        setParam(MAXLONGITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMaxLongitude() {
        clearParam(MAXLONGITUDE);
        return this;
    }


    public static final String LATITUDE = "latitude";

    /** Specify the latitude to be used for a radius search.
     */
    public FDSNEventQueryParams setLatitude(float value) {
        setParam(LATITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearLatitude() {
        clearParam(LATITUDE);
        return this;
    }


    public static final String LONGITUDE = "longitude";

    /** Specify the longitude to the used for a radius search.
     */
    public FDSNEventQueryParams setLongitude(float value) {
        setParam(LONGITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearLongitude() {
        clearParam(LONGITUDE);
        return this;
    }


    public static final String MINRADIUS = "minradius";

    /** Limit to events within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.
     */
    public FDSNEventQueryParams setMinRadius(float value) {
        setParam(MINRADIUS, value);
        return this;
    }

    public FDSNEventQueryParams clearMinRadius() {
        clearParam(MINRADIUS);
        return this;
    }


    public static final String MAXRADIUS = "maxradius";

    /** Limit to events within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.
     */
    public FDSNEventQueryParams setMaxRadius(float value) {
        setParam(MAXRADIUS, value);
        return this;
    }

    public FDSNEventQueryParams clearMaxRadius() {
        clearParam(MAXRADIUS);
        return this;
    }


    public static final String MINDEPTH = "mindepth";

    /** Limit to events with depth more than the specified minimum.
     */
    public FDSNEventQueryParams setMinDepth(float value) {
        setParam(MINDEPTH, value);
        return this;
    }

    public FDSNEventQueryParams clearMinDepth() {
        clearParam(MINDEPTH);
        return this;
    }


    public static final String MAXDEPTH = "maxdepth";

    /** Limit to events with depth less than the specified maximum.
     */
    public FDSNEventQueryParams setMaxDepth(float value) {
        setParam(MAXDEPTH, value);
        return this;
    }

    public FDSNEventQueryParams clearMaxDepth() {
        clearParam(MAXDEPTH);
        return this;
    }


    public static final String MINMAGNITUDE = "minmagnitude";

    /** Limit to events with a magnitude larger than the specified minimum.
     */
    public FDSNEventQueryParams setMinMagnitude(float value) {
        setParam(MINMAGNITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMinMagnitude() {
        clearParam(MINMAGNITUDE);
        return this;
    }


    public static final String MAXMAGNITUDE = "maxmagnitude";

    /** Limit to events with a magnitude smaller than the specified maximum.
     */
    public FDSNEventQueryParams setMaxMagnitude(float value) {
        setParam(MAXMAGNITUDE, value);
        return this;
    }

    public FDSNEventQueryParams clearMaxMagnitude() {
        clearParam(MAXMAGNITUDE);
        return this;
    }


    public static final String MAGNITUDETYPE = "magnitudetype";

    /** Specify a magnitude type to use for testing the minimum and maximum limits.
     */
    public FDSNEventQueryParams setMagnitudeType(String value) {
        setParam(MAGNITUDETYPE, value);
        return this;
    }

    public FDSNEventQueryParams clearMagnitudeType() {
        clearParam(MAGNITUDETYPE);
        return this;
    }


    public static final String INCLUDEALLORIGINS = "includeallorigins";

    /** Specify if all origins for the event should be included, default is data center dependent but is suggested to be the preferred origin only.
     */
    public FDSNEventQueryParams setIncludeAllOrigins(boolean value) {
        setParam(INCLUDEALLORIGINS, value);
        return this;
    }

    public FDSNEventQueryParams clearIncludeAllOrigins() {
        clearParam(INCLUDEALLORIGINS);
        return this;
    }


    public static final String INCLUDEALLMAGNITUDES = "includeallmagnitudes";

    /** Specify if all magnitudes for the event should be included, default is data center dependent but is suggested to be the preferred magnitude only.
     */
    public FDSNEventQueryParams setIncludeAllMagnitudes(boolean value) {
        setParam(INCLUDEALLMAGNITUDES, value);
        return this;
    }

    public FDSNEventQueryParams clearIncludeAllMagnitudes() {
        clearParam(INCLUDEALLMAGNITUDES);
        return this;
    }


    public static final String INCLUDEARRIVALS = "includearrivals";

    /** Specify if phase arrivals should be included.
     */
    public FDSNEventQueryParams setIncludeArrivals(boolean value) {
        setParam(INCLUDEARRIVALS, value);
        return this;
    }

    public FDSNEventQueryParams clearIncludeArrivals() {
        clearParam(INCLUDEARRIVALS);
        return this;
    }


    public static final String EVENTID = "eventid";

    /** Select a specific event by ID; event identifiers are data center specific.
     */
    public FDSNEventQueryParams setEventid(String value) {
        setParam(EVENTID, value);
        return this;
    }

    public FDSNEventQueryParams clearEventid() {
        clearParam(EVENTID);
        return this;
    }


    public static final String LIMIT = "limit";

    /** Limit the results to the specified number of events.
     */
    public FDSNEventQueryParams setLimit(int value) {
        setParam(LIMIT, value);
        return this;
    }

    public FDSNEventQueryParams clearLimit() {
        clearParam(LIMIT);
        return this;
    }


    public static final String OFFSET = "offset";

    /** Return results starting at the event count specified, starting at 1.
     */
    public FDSNEventQueryParams setOffset(int value) {
        setParam(OFFSET, value);
        return this;
    }

    public FDSNEventQueryParams clearOffset() {
        clearParam(OFFSET);
        return this;
    }


    public static final String ORDERBY = "orderby";

    /** Order the result by time or magnitude with the following possibilities: time: order by origin descending time time-asc : order by origin ascending time magnitude: order by descending magnitude magnitude-asc : order by ascending magnitude
     */
    public FDSNEventQueryParams setOrderBy(String value) {
        setParam(ORDERBY, value);
        return this;
    }

    public FDSNEventQueryParams clearOrderBy() {
        clearParam(ORDERBY);
        return this;
    }


    public static final String CATALOG = "catalog";

    /** Limit to events from a specified catalog
     */
    public FDSNEventQueryParams setCatalog(String value) {
        setParam(CATALOG, value);
        return this;
    }

    public FDSNEventQueryParams clearCatalog() {
        clearParam(CATALOG);
        return this;
    }


    public static final String CONTRIBUTOR = "contributor";

    /** Limit to events contributed by a specified contributor.
     */
    public FDSNEventQueryParams setContributor(String value) {
        setParam(CONTRIBUTOR, value);
        return this;
    }

    public FDSNEventQueryParams clearContributor() {
        clearParam(CONTRIBUTOR);
        return this;
    }


    public static final String UPDATEDAFTER = "updatedafter";

    /** Limit to events updated after the specified time.
     */
    public FDSNEventQueryParams setUpdatedAfter(Date value) {
        setParam(UPDATEDAFTER, value);
        return this;
    }

    public FDSNEventQueryParams clearUpdatedAfter() {
        clearParam(UPDATEDAFTER);
        return this;
    }


    


    public FDSNEventQueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
        return setMinLatitude(minLat).setMaxLatitude(maxLat).setMinLongitude(minLon).setMaxLongitude(maxLon);
    }

    public FDSNEventQueryParams ring(float lat, float lon, float maxRadius) {
        return setLatitude(lat).setLongitude(lon).setMaxRadius(maxRadius);
    }

    public FDSNEventQueryParams donut(float lat, float lon, float minRadius, float maxRadius) {
        return ring(lat, lon, maxRadius).setMinRadius(minRadius);
    }

    /** time: order by origin descending time */
    public static final String ORDER_TIME = "time";

    /** time-asc : order by origin ascending time */
    public static final String ORDER_TIME_ASC = "time-asc";

    /**magnitude: order by descending magnitude */
    public static final String ORDER_MAGNITUDE = "magnitude";

    /**magnitude-asc : order by ascending magnitude*/
    public static final String ORDER_MAGNITUDE_ASC = "magnitude-asc";


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

