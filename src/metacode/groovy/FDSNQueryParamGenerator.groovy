/*
  Run with
  groovy FDSNQueryParamGenerator
  from within the src/metacode/groovy directory.
*/
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import groovy.text.SimpleTemplateEngine

class FDSNQueryParamGenerator {
    def engine = new SimpleTemplateEngine()

    String createItem(key, doc, service) {
        String t = ""
        String shortName = null
        if (key in shortNames) shortName = shortNames[key]
        if (key in dateTypes) {t="Instant";}
        else if(key in floatTypes) {t="float"}
        else if (key in intTypes) {t="int"}
        else if (key in booleanTypes) {t="boolean"}
        String setter = 'set'
        String arrayType = ''
        if (key in addTypes) { arrayType = "[]" }
        String locidSpaceCheck = ""
        if (key in locIdTypes) {locidSpaceCheck = 'if (value == null || Channel.EMPTY_LOC_CODE.equals(value.trim())) { value = "--";}\n        ' }
        String optionNames = '"--'+key.toLowerCase()+'"'
        if (shortName != null) { optionNames+=',"--'+shortName+'"' }
        if (key in singleCharNames) { optionNames = '"-'+singleCharNames[key]+ '",'+optionNames }
        def binding = ['key':key,
                       'optionNames':optionNames,
                       'doc':doc, 'type':t,
                       'service':service,
                       'setter':setter,
                       'locidSpaceCheck':locidSpaceCheck,
                       'shortName':shortName,
                       'arrayType':arrayType]
        String out = ""
        if (key in shortNames) {
            out += engine.createTemplate(templateTextShortConst).make(binding)
        } else {
            out += engine.createTemplate(templateTextConst).make(binding)
        }
        out += engine.createTemplate(templateTextClear).make(binding)
        if (arrayType == "[]") {
          out += engine.createTemplate(templateTextAppendTo).make(binding)
        } else {
          out += engine.createTemplate(templateTextSet).make(binding)
        }
    }


    String createOptionItem(key, doc, service) {
        String t = ""
        String shortName = null
        String converter = ''
        if (key in shortNames) shortName = shortNames[key]
        if (key in dateTypes) {
            t="Instant";
            converter=', converter=FloorISOTimeParser.class';
            if (key.equals('endTime') || key.endsWith('Before')) {
                converter=', converter=CeilingISOTimeParser.class';
            }
        }
        else if(key in floatTypes) {t="float"}
        else if (key in intTypes) {t="int"}
        else if (key in booleanTypes) {t="boolean"}
        else if (key=="level") {t="QueryLevel"}
        String setter = 'set'
        String arrayType = ''
        if (key in addTypes) { arrayType = "[]" }
        String locidSpaceCheck = ""
        if (key in locIdTypes) {locidSpaceCheck = 'if (value == null || Channel.EMPTY_LOC_CODE.equals(value.trim())) { value = "--";}\n        ' }
        String optionNames = '"--'+key.toLowerCase()+'"'
        if (shortName != null) { optionNames+=',"--'+shortName+'"' }
        if (key in singleCharNames) { optionNames = '"-'+singleCharNames[key]+ '",'+optionNames }
        def binding = ['key':key,
                       'optionNames':optionNames,
                       'doc':doc, 'type':t,
                       'service':service,
                       'setter':setter,
                       'locidSpaceCheck':locidSpaceCheck,
                       'shortName':shortName,
                       'arrayType':arrayType,
                       'converter':converter]
        String out = ""
        if (arrayType == "[]") {
          out += engine.createTemplate(templateTextOptionArray).make(binding)
        } else {
          out += engine.createTemplate(templateTextOptionSet).make(binding)
        }
    }


    String createTestItem(key, doc, service) {
        String t = ""
        String shortName = null
        String converter = ''
        String preargs = ''
        if (service in testingpreargs) preargs = testingpreargs[service]
        if (key in shortNames) shortName = shortNames[key]
        if (key in dateTypes) {
            t="Instant";
            converter=', converter=FloorISOTimeParser.class';
            if (key.startsWith('end')) {
                converter=', converter=CeilingISOTimeParser.class';
            }
        }
        else if(key in floatTypes) {t="float"}
        else if (key in intTypes) {t="int"}
        else if (key in booleanTypes) {t="boolean"}
        String setter = 'set'
        String arrayType = ''
        if (key in addTypes) { arrayType = "[]" }
        String locidSpaceCheck = ""
        if (key in locIdTypes) {locidSpaceCheck = 'if (value == null || Channel.EMPTY_LOC_CODE.equals(value.trim())) { value = "--";}\n        ' }
        String optionNames = '"--'+key.toLowerCase()+'"'
        if (shortName != null) { optionNames+=',"--'+shortName+'"' }
        if (key in singleCharNames) { optionNames = '"-'+singleCharNames[key]+ '",'+optionNames }
        String data = 'data'
        if (key in dateTypes) {
            data = '2021-01-01'
            if (key.startsWith('end')) {
                data = '2021-01-02'
            }
        }
        else if (key in floatTypes || key in intTypes) {if (key.startsWith("min")) { data = '5'} else {data = "10"} }
        else if (key in intTypes) {t="int"}
        else if (key in booleanTypes) {data=''}
        def binding = ['key':key,
                       'optionNames':optionNames,
                       'doc':doc, 'type':t,
                       'service':service,
                       'setter':setter,
                       'locidSpaceCheck':locidSpaceCheck,
                       'shortName':shortName,
                       'arrayType':arrayType,
                       'converter':converter,
                       'data':data,
                       'preargs': preargs ]
        String out = ""
        if (key in shortNames) {
            out += engine.createTemplate(templateTestingShortConst).make(binding)
        } else {
            out += engine.createTemplate(templateTestingConst).make(binding)
        }
        if (arrayType == "[]") {
          out += engine.createTemplate(templateTestingArray).make(binding)
        }
        return out;
    }

    String createPre(service) {
        def binding = ['service':service]
        return engine.createTemplate(preTemplate).make(binding)
    }

    String createPost(service) {
        def ws = service.toLowerCase()
        def extra = engine.createTemplate(extraPostCode[service]).make(['service':service, 'ws':ws])
        def binding = ['service':service, 'ws':ws, 'extra':extra]
        return engine.createTemplate(postTemplate).make(binding)
    }

    def preTemplate = '''
package edu.sc.seis.seisFile.fdsnws;

import java.time.Instant;

import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.*;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSN${service.capitalize()}QueryParams extends AbstractQueryParams implements Cloneable {

    public FDSN${service}QueryParams() {
        this(${service=='Event'?'USGS_HOST':'DEFAULT_HOST'});
    }

    public FDSN${service}QueryParams(String host) {
        super(host==null ? ${service=='Event'?'USGS_HOST':'DEFAULT_HOST'} : host);
    }

    public FDSN${service}QueryParams clone() {
        FDSN${service}QueryParams out = new FDSN${service}QueryParams(getHost());
        out.cloneNonParams(this);
        for (String key : params.keySet()) {
            out.setParam(key, params.get(key));
        }
        return out;
    }

    public FDSN${service}QueryParams setHost(String host) {
        this.host = host;
        return this;
    }
    public FDSN${service}QueryParams setPort(int port) {
        this.port = port;
        return this;
    }
'''

    def postTemplate = '''
    ${extra}

}
'''

    def templateTextConst = '''
    public static final String ${key.toUpperCase()} = "${key.toLowerCase()}";

'''

    def templateTextShortConst = '''
    public static final String ${key.toUpperCase()} = "${key.toLowerCase()}";

    public static final String ${key.toUpperCase()}_SHORT = "${shortName.toLowerCase()}";
'''


    def templateTextSet = '''
    /** $doc
     *  @param value value to set
     *  @return the queryParams for method chaining
     */
    public FDSN${service}QueryParams ${setter}${key.capitalize()}(${type==''?'String':type}${arrayType} value) {
        ${locidSpaceCheck}${setter}Param(${key.toUpperCase()}, value);
        return this;
    }
'''

    def templateTextClear = '''

    public FDSN${service}QueryParams clear${key.capitalize()}() {
        clearParam(${key.toUpperCase()});
        return this;
    }
'''

    def templateTextAppendTo = '''

    /** $doc
     */
    public FDSN${service}QueryParams ${setter}${key.capitalize()}(${type==''?'String':type}${arrayType} value) {
        clear${key.capitalize()}();
        for(${type==''?'String':type} v: value) appendTo${key.capitalize()}(v);
        return this;
    }

    public FDSN${service}QueryParams appendTo${key.capitalize()}(${type==''?'String':type} value) {
        ${locidSpaceCheck}appendToParam(${key.toUpperCase()}, value);
        return this;
    }
'''

// Cmd Line Options....


    def preOptionTemplate = '''
package edu.sc.seis.seisFile.client;

import java.time.Instant;

import edu.sc.seis.seisFile.*;
import edu.sc.seis.seisFile.fdsnws.FDSN${service.capitalize()}QueryParams;
import edu.sc.seis.seisFile.fdsnws.QueryLevel;
import picocli.CommandLine.Option;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSN${service.capitalize()}CmdLineQueryParams {

    FDSN${service.capitalize()}QueryParams queryParams;

    public FDSN${service}CmdLineQueryParams() {
        this(FDSN${service.capitalize()}QueryParams.${service=='Event'?'USGS_HOST':'DEFAULT_HOST'});
    }

    public FDSN${service}CmdLineQueryParams(String host) {
        this.queryParams = new FDSN${service.capitalize()}QueryParams();
        setHost(host==null ? FDSN${service.capitalize()}QueryParams.${service=='Event'?'USGS_HOST':'DEFAULT_HOST'} : host);
    }

    @Option(names = { "--host" }, description="host to connect to, defaults to \\${DEFAULT-VALUE}", defaultValue=FDSN${service.capitalize()}QueryParams.${service=='Event'?'USGS_HOST':'DEFAULT_HOST'})
    public FDSN${service}QueryParams setHost(String host) {
        return this.queryParams.setHost(host);
    }

    @Option(names = "--port", description = "port to connect to, defaults to \\${DEFAULT-VALUE}", defaultValue="80")
    public FDSN${service}QueryParams setPort(int port) {
        return this.queryParams.setPort(port);
    }
'''

    def postOptionTemplate = '''
    ${extra}

}
'''

    def templateTextOptionSet = '''
    /** $doc
     */
    @Option(names = { ${optionNames} }, description="$doc"${converter} )
    public FDSN${service}QueryParams ${setter}${key.capitalize()}(${type==''?'String':type}${arrayType} value) {
        queryParams.${setter}${key.capitalize()}(value);
        return queryParams;
    }
'''

    def templateTextOptionArray = '''

    @Option(names = { ${optionNames} }, description="$doc", split = ","${converter} )
    public FDSN${service}QueryParams set${key.capitalize()}(${type==''?'String':type}${arrayType} value) {
      queryParams.clear${key.capitalize()}();
      for(${type==''?'String':type} v: value) queryParams.appendTo${key.capitalize()}(v);
      return queryParams;
    }
'''

    def templateTestingConst = '''
    bin/fdsn${service.toLowerCase()} ${preargs}  --${key.toLowerCase()} ${data}

'''

    def templateTestingShortConst = '''
    bin/fdsn${service.toLowerCase()} ${preargs} --${shortName.toLowerCase()} ${data}
'''

    def templateTestingArray = '''
    bin/fdsn${service.toLowerCase()} ${preargs} --${shortName.toLowerCase()} ${data} --${shortName.toLowerCase()} ${data}
'''


    def addTypes = ['network', 'station', 'location', 'channel']

    def locIdTypes = ['location', 'loc']

    def dateTypes = ['startTime', 'endTime', 'startBefore', 'startAfter', 'endBefore', 'endAfter', 'updatedAfter']

    def intTypes = ['minimumLength', 'limit', 'offset']

    def floatTypes = ['minLatitude', 'maxLatitude', 'minLongitude', 'maxLongitude', 'latitude', 'longitude', 'minRadius', 'maxRadius', 'minMagnitude', 'maxMagnitude', 'minDepth', 'maxDepth']

    def booleanTypes = ['longestOnly', 'includeRestricted', 'includeAvailability', 'matchTimeseries', 'includeAllOrigins', 'includeAllMagnitudes', 'includeArrivals']

    def singleCharNames = [
        'network':'n', 'station':'s', 'location':'l', 'channel':'c',
        'level': 'L',
        'magnitudeType':'t', 'catalog':'c', 'contributor':'C', 'depth':'D',
        'startTime':'b', 'endTime':'e'
        ]

    def shortNames = ['startTime':'start', 'endTime':'end',
        'network':'net', 'station':'sta', 'location':'loc', 'channel':'cha',
        'minLatitude':'minlat',  'maxLatitude':'maxlat', 'minLongitude':'minlon', 'maxLongitude':'maxlon',
        'latitude':'lat', 'longitude':'lon',
        'minMagnitude':'minmag', 'maxMagnitude':'maxmag',  'magnitudeType':'magtype']

    def dataSelectParams = ['startTime':'Limit results to time series samples on or after the specified start time',
        'endTime':'Limit results to time series samples on or before the specified end time',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case -- (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'quality':'Select a specific SEED quality indicator, handling is data center dependent.',
        'minimumLength':'Limit results to continuous data segments of a minimum length specified in seconds.',
        'longestOnly':'Limit results to the longest continuous segment per channel.']

    def stationParams = ['startTime':'Limit to metadata epochs starting on or after the specified start time.',
        'endTime':'Limit to metadata epochs ending on or before the specified end time.',
        'startBefore':'Limit to metadata epochs starting before specified time.',
        'startAfter':'Limit to metadata epochs starting after specified time.',
        'endBefore':'Limit to metadata epochs ending before specified time.',
        'endAfter':'Limit to metadata epochs ending after specified time.',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case -- (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'minLatitude':'Limit to stations with a latitude larger than the specified minimum.',
        'maxLatitude':'Limit to stations with a latitude smaller than the specified maximum.',
        'minLongitude':'Limit to stations with a longitude larger than the specified minimum.',
        'maxLongitude':'Limit to stations with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minRadius':'Limit results to stations within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxRadius':'Limit results to stations within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'level':'Specify the level of detail for the results.',
        'includeRestricted':'Specify if results should include information for restricted stations.',
        'includeAvailability':'Specify if results should include information about time series data availability.',
        'matchTimeseries':'Limit to metadata where selection criteria matches time series data availability.',
        'updatedAfter':'Limit to metadata updated after specified date; updates are data center specific.']

    def eventParams = ['startTime':'Limit to events on or after the specified start time.',
        'endTime':'Limit to events on or before the specified end time.',
        'minLatitude':'Limit to events with a latitude larger than the specified minimum.',
        'maxLatitude':'Limit to events with a latitude smaller than the specified maximum.',
        'minLongitude':'Limit to events with a longitude larger than the specified minimum.',
        'maxLongitude':'Limit to events with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minRadius':'Limit to events within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxRadius':'Limit to events within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'minDepth':'Limit to events with depth more than the specified minimum.',
        'maxDepth':'Limit to events with depth less than the specified maximum.',
        'minMagnitude':'Limit to events with a magnitude larger than the specified minimum.',
        'maxMagnitude':'Limit to events with a magnitude smaller than the specified maximum.',
        'magnitudeType':'Specify a magnitude type to use for testing the minimum and maximum limits.',
        'includeAllOrigins':'Specify if all origins for the event should be included, default is data center dependent but is suggested to be the preferred origin only.',
        'includeAllMagnitudes':'Specify if all magnitudes for the event should be included, default is data center dependent but is suggested to be the preferred magnitude only.',
        'includeArrivals':'Specify if phase arrivals should be included.',
        'eventid':'Select a specific event by ID; event identifiers are data center specific.',
        'limit':'Limit the results to the specified number of events.',
        'offset':'Return results starting at the event count specified, starting at 1.',
        'orderBy':'Order the result by time or magnitude with the following possibilities: time: order by origin descending time time-asc : order by origin ascending time magnitude: order by descending magnitude magnitude-asc : order by ascending magnitude',
        'catalog':'Limit to events from a specified catalog',
        'contributor':'Limit to events contributed by a specified contributor.',
        'updatedAfter':'Limit to events updated after the specified time.'
    ]

    def areaMethods = '''

    public FDSN${service}QueryParams boxArea(BoxArea box) {
        return area(box.south, box.north, box.west, box.east);
    }

    public FDSN${service}QueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
        return setMinLatitude(minLat).setMaxLatitude(maxLat).setMinLongitude(minLon).setMaxLongitude(maxLon);
    }

    public FDSN${service}QueryParams ring(float lat, float lon, float maxRadius) {
        return setLatitude(lat).setLongitude(lon).setMaxRadius(maxRadius);
    }

    public FDSN${service}QueryParams donut(DonutArea donut) {
        return ring(donut.latitude, donut.longitude, donut.maxradius).setMinRadius(donut.minradius);
    }
'''

    def optionAreaMethods = '''

        @Option(names = {"--box"}, description="constraining box as west/east/south/north",
                converter=BoxAreaParser.class, paramLabel="w/e/s/n")
        public FDSN${service}QueryParams boxArea(BoxArea box) {
            return queryParams.boxArea(box);
        }

        public FDSN${service}QueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
            return queryParams.area( minLat, maxLat, minLon, maxLon);
        }

        public FDSN${service}QueryParams ring(float lat, float lon, float maxRadius) {
            return queryParams.ring( lat,  lon,  maxRadius);
        }

        @Option(names = {"--donut"}, description="constraining donut as lat/lon/minRadius/maxRadius",
                converter=DonutParser.class, paramLabel="lat/lon/min/max")
        public FDSN${service}QueryParams donut(DonutArea donut) {
            return queryParams.ring(donut.latitude, donut.longitude, donut.maxradius).setMinRadius(donut.minradius);
        }
    '''

    def extraPostCode = ['Station':'''

    public static final String LEVEL_NETWORK = QueryLevel.network.name();

    public static final String LEVEL_STATION = QueryLevel.station.name();

    public static final String LEVEL_CHANNEL = QueryLevel.channel.name();

    public static final String LEVEL_RESPONSE = QueryLevel.response.name();

    @Override
    public String getServiceName() {
        return STATION_SERVICE;
    }

    public static final String STATION_SERVICE = "station";
''',
        'Event':'''

    public static final String USGS_HOST = "earthquake.usgs.gov";
    public static final String ISC_HOST = "www.isc.ac.uk";
    public static final String ISC_MIRROR_HOST = "isc-mirror.iris.washington.edu";

    /** time: order by origin descending time */
    public static final String ORDER_TIME = "time";

    /** time-asc : order by origin ascending time */
    public static final String ORDER_TIME_ASC = "time-asc";

    /**magnitude: order by descending magnitude */
    public static final String ORDER_MAGNITUDE = "magnitude";

    /**magnitude-asc : order by ascending magnitude*/
    public static final String ORDER_MAGNITUDE_ASC = "magnitude-asc";

    @Override
    public String getServiceName() {
        return EVENT_SERVICE;
    }

    public static final String EVENT_SERVICE = "event";
''',
        'DataSelect':'''

    /**
     * Forms the list of ChannelTimeWindow for use in a POST request to
     * the web service. All possible combinations of Networks, Stations
     * and Channels are combined with the start and end times to form
     * the list of ChannelTimeWindow.
     *
     * @return List of Channels paired with the time window
     */
    public java.util.List<ChannelTimeWindow> createChannelTimeWindow() {
        java.util.List<ChannelTimeWindow> request = new java.util.ArrayList<ChannelTimeWindow>();
        String[] netSplit = getParam(NETWORK).split(",");
        String[] staSplit = getParam(STATION).split(",");
        String[] locSplit = getParam(LOCATION).split(",");
        String[] chanSplit = getParam(CHANNEL).split(",");
        Instant beginDate = TimeUtils.parseISOString(getParam(STARTTIME));
        Instant endDate = TimeUtils.parseISOString(getParam(ENDTIME));
            for (int n = 0; n < netSplit.length; n++) {
                for (int s = 0; s < staSplit.length; s++) {
                    for (int l = 0; l < locSplit.length; l++) {
                        for (int c = 0; c < chanSplit.length; c++) {
                            request.add(new ChannelTimeWindow(netSplit[n],
                                                              staSplit[s],
                                                              locSplit[l],
                                                              chanSplit[c],
                                                              beginDate,
                                                              endDate));
                        }
                    }
                }
            }
        return request;
    }

    public String formPostString() {
        return formPostString(createChannelTimeWindow());
    }

    /**
     * Forms the text for use in a POST request to the web service. Channel and
     * time window are taken from the list of ChannelTimeWindow.
     *
     * @return
     */
    public String formPostString(java.util.List<ChannelTimeWindow> request) {
        StringBuffer out = new StringBuffer();
        if (getParam(QUALITY) != null) {
            out.append(QUALITY + "=" + getParam(QUALITY) + NEWLINE);
        }
        if (getParam(MINIMUMLENGTH) != null) {
            out.append(MINIMUMLENGTH + "=" + getParam(MINIMUMLENGTH) + NEWLINE);
        }
        if (getParam(LONGESTONLY) != null) {
            out.append(LONGESTONLY + "=" + getParam(LONGESTONLY) + NEWLINE);
        }
        String SEP = " ";
        for (ChannelTimeWindow ctw : request) {
            out.append(ctw.formString(SEP, createDateFormat(), true)+NEWLINE);
        }
        return out.toString();
    }

    @Override
    public String getServiceName() {
        return DATASELECT_SERVICE;
    }

    public static final String DATASELECT_SERVICE = "dataselect";

''']


    def optionPostCode = ['Station':'''

    public String getServiceName() {
        return queryParams.getServiceName();
    }

''',
        'Event':'''

    @Option(names={"-m", "--magnitude"}, arity="1..2", description="The range of acceptable magnitudes, max may be omitted.")
    public FDSN${service}QueryParams setMagnitudeRange(float[] minmax) {
        if (minmax.length >1) {
            setMaxMagnitude(minmax[1]);
        }
        return setMinMagnitude(minmax[0]);
    }

    public String getServiceName() {
        return queryParams.getServiceName();
    }

''',
        'DataSelect':'''

    @Option(names = {"--post"}, description="use http POST instead of GET")
    boolean doPost = false;

    public String getServiceName() {
        return queryParams.getServiceName();
    }
''']

   def testingpreargs = ['Station': '-n CO -s BIRD', 'Event': '--box -85/-75/29/35', 'DataSelect': '-n CO -s BIRD']


    public static void main(String[] args) {
        def x = new FDSNQueryParamGenerator()
        def data = ['Station':x.stationParams, 'Event':x.eventParams, 'DataSelect':x.dataSelectParams]
        for (s in ['Station', 'Event', 'DataSelect'])  {
            new File("../../main/java/edu/sc/seis/seisFile/fdsnws/FDSN${s}QueryParams.java").withWriter { writer ->
                writer.println x.createPre(s)
                data.get(s).each() { k, v ->
                    writer.println x.createItem(k, v, s)
                }
                if (s != 'DataSelect') {
                    writer.println x.engine.createTemplate(x.areaMethods).make(['service':s])
                }
                writer.println x.createPost(s)
            }
            new File("../../client/java/edu/sc/seis/seisFile/client/FDSN${s}CmdLineQueryParams.java").withWriter { writer ->
                def ws = s.toLowerCase()
                def extra = ""
                def binding = ['service':s, 'ws':ws, 'extra':extra]
                writer.println x.engine.createTemplate(x.preOptionTemplate).make(binding)
                data.get(s).each() { k, v ->
                    writer.println x.createOptionItem(k, v, s)
                }
                if (s != 'DataSelect') {
                    writer.println x.engine.createTemplate(x.optionAreaMethods).make(['service':s])
                }

                writer.println x.engine.createTemplate(x.optionPostCode[s]).make(binding)
                writer.println x.engine.createTemplate(x.postOptionTemplate).make(binding)
            }
            new File("../../test/resources/FDSN${s}CmdLineTest.txt").withWriter { writer ->
                data.get(s).each() { k, v ->
                    writer.println x.createTestItem(k, v, s)
                }
            }
        }
    }
}
