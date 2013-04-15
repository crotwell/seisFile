
import java.net.URI;
import java.net.URISyntaxException;

import groovy.text.SimpleTemplateEngine

class FDSNStationQueryParamGenerator {
    def engine = new SimpleTemplateEngine()

    String createItem(key, doc) {
        String t = ""
        if (key in dateTypes) {t="Date"}
        else if(key in floatTypes) {t="Float"}
        else if (key in intTypes) {t="Integer"}
        def binding = ['key':key, 'doc':doc, 'type':t]
        return engine.createTemplate(templateText).make(binding)
    }

    String createPre(key) {
        def binding = ['key':key]
        return engine.createTemplate(preTemplate).make(binding)
    }

    String createPost(key) {
        def ws = key.toLowerCase()
        if (key == 'QuakeML') { ws = 'event' }
        else if(key == 'StationXML') { ws = 'station' }
        else if(key == 'DataSelect') { ws = 'dataselect' }
        def binding = ['key':key, 'ws':ws]
        return engine.createTemplate(postTemplate).make(binding)
    }

    def preTemplate = '''
package edu.sc.seis.seisFile.fdsnws;


import java.net.URI;
import java.net.URISyntaxException;

public class FDSN${key.capitalize()}QueryParams extends AbstractQueryParams {

    public FDSN${key}QueryParams() {
        this(IRIS_BASE_URI);
    }
    
    public FDSN${key}QueryParams(URI baseUri) {
        super(baseUri);
    }

'''

    def postTemplate = '''

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/${ws}/1/query?";
    
    public static final URI IRIS_BASE_URI;
    
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string"+IRIS_BASE_URL);
        }
    }
}
'''

    def templateText = '''
    public static final String ${key.toUpperCase()} = "$key";

    /** $doc
     */
    public FDSNQuakeMLQueryParams $key($type value) {
        set${type}Param(${key.toUpperCase()}, value);
        return this;
    }
'''


    def dataSelectParams = ['starttime':'Limit results to time series samples on or after the specified start time',
        'endtime':'Limit results to time series samples on or before the specified end time',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case Ò--Ò (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'quality':'Select a specific SEED quality indicator, handling is data center dependent.',
        'minimumlength':'Limit results to continuous data segments of a minimum length specified in seconds.',
        'longestonly':'Limit results to the longest continuous segment per channel.']

    def dateTypes = ['starttime', 'endtime', 'startbefore', 'startafter', 'endbefore', 'endafter', 'updatedafter']

    def intTypes = ['minimumlength', 'limit', 'offset']

    def floatTypes = ['minlatitude', 'maxlatitude', 'minlongitude', 'maxlongitude', 'latitude', 'longitude', 'minradius', 'maxradius', 'minmagnitude', 'maxmagnitude']

    def booleanTypes = ['longestonly', 'includerestricted', 'includeavailability', 'includeallorigins', 'includeallmagnitudes', 'includearrivals']

    def stationParams = ['starttime':'Limit tometadata epochs startingon or after the specified start time.',
        'endtime':'Limit to metadata epochs ending on or before the specified end time.',
        'startbefore':'Limit to metadata epochs starting before specified time.',
        'startafter':'Limit to metadata epochs starting after specified time.',
        'endbefore':'Limit to metadata epochs ending before specified time.',
        'endafter':'Limit to metadata epochs ending after specified time.',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case Ò--Ò (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'minlatitude':'Limit to stations with a latitude larger than the specified minimum.',
        'maxlatitude':'Limit to stations with a latitude smaller than the specified maximum.',
        'minlongitude':'Limit to stations with a longitude larger than the specified minimum.',
        'maxlongitude':'Limit to stations with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minradius':'Limit results to stations within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxradius':'Limit results to stations within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'level':'Specify the level of detail for the results.',
        'includerestricted':'Specify if results should include information for restricted stations.',
        'includeavailability':'Specify if results should include information about time series data availability.',
        'updatedafter':'Limit to metadata updated after specified date; updates are data center specific.']

    def stationTypes = ['starttime':'Date', 'endtime':'Date', ]

    def eventParams = ['starttime':'Limit to events on or after the specified start time.',
        'endtime':'Limit to events on or before the specified end time.',
        'minlatitude':'Limit to events with a latitude larger than the specified minimum.',
        'maxlatitude':'Limit to events with a latitude smaller than the specified maximum.',
        'minlongitude':'Limit to events with a longitude larger than the specified minimum.',
        'maxlongitude':'Limit to events with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minradius':'Limit to events within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxradius':'Limit to events within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'mindepth':'Limit to events with depth more than the specified minimum.',
        'maxdepth':'Limit to events with depth less than the specified maximum.',
        'minmagnitude':'Limit to events with a magnitude larger than the specified minimum.',
        'maxmagnitude':'Limit to events with a magnitude smaller than the specified maximum.',
        'magnitudetype':'Specify a magnitude type to use for testing the minimum and maximum limits.',
        'includeallorigins':'Specify if all origins for the event should be included, default is data center dependent but is suggested to be the preferred origin only.',
        'includeallmagnitudes':'Specify if all magnitudes for the event should be included, default is data center dependent but is suggested to be the preferred magnitude only.',
        'includearrivals':'Specify if phase arrivals should be included.',
        'eventid':'Select a specific event by ID; event identifiers are data center specific.',
        'limit':'Limit the results to the specified number of events.',
        'offset':'Return results starting at the event count specified, starting at 1.',
        'orderby':'Order the result by time or magnitude with the following possibilities: time: order by origin descending time time-asc : order by origin ascending time magnitude: order by descending magnitude magnitude-asc : order by ascending magnitude',
        'catalog':'Limit to events from a specified catalog',
        'contributor':'Limit to events contributed by a specified contributor.',
        'updatedafter':'Limit to events updated after the specified time.'
    ]

    public static void main(String[] args) {
        def x = new FDSNStationQueryParamGenerator()
        for (s in ['Station', 'Event', 'DataSelect'])  {
            new File("FDSN${s}QueryParams.java").withWriter { writer ->
                writer.println x.createPre(s)
                x.stationParams.each() { k, v ->
                    writer.println x.createItem(k, v)
                }
                writer.println x.createPost(s)
            }
        }
    }
}
