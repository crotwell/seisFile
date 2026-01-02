package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.sc.seis.seisFile.ISOTimeParser;
import edu.sc.seis.seisFile.TimeUtils;

public abstract class AbstractQueryParams {

    public AbstractQueryParams(String host) {
        this.host = host;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void setParam(String key, int value) {
        params.put(key, "" + value);
    }

    public void setParam(String key, float value) {
        params.put(key, "" + value);
    }

    public void setParam(String key, boolean value) {
        params.put(key, value ? "true" : "false");
    }

    public void appendToParam(String key, String value) {
        if (params.containsKey(key)) {
            if (params.get(key).equals(value) 
                    || params.get(key).startsWith(value+",") 
                    || params.get(key).endsWith(","+value) 
                    || params.get(key).contains(","+value+",")) {
                // already in list
                value = params.get(key);
            } else {
                // not already in list
                value = params.get(key) + "," + value;
            }
        }
        params.put(key, value);
    }

    public void setParam(String key, Instant value) {
        setParam(key, value, false);
    }
    public void setParam(String key, Instant value, boolean appendZ) {
        String t;
        if (appendZ) {
            t = ISOTimeParser.formatWithTimezone(value);
        } else {
            t= ISOTimeParser.format(value);
        }
        setParam(key, t);
    }

    public void clearParam(String key) {
        params.remove(key);
    }

    public void clear() {
        params.clear();
    }

    public static DateTimeFormatter createDateFormat() {
        return TimeUtils.createFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    public URI formURI() throws URISyntaxException {
        if (host.equalsIgnoreCase(EARTHSCOPE_HOST)) {
            useHTTPS();
        }
        params = getParams();
        if (params.size() == 0) {
            throw new IllegalArgumentException("at least one parameter is required");
        }
        StringBuilder newQuery = new StringBuilder();
        if (newQuery.length() != 0) {
            newQuery.append("&");
        }
        List<String> keyList = new ArrayList<String>();
        keyList.addAll(params.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            if (params.get(key) != null && params.get(key).length() != 0) {
                newQuery.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        if (newQuery.length() > 1) {
            newQuery.deleteCharAt(newQuery.length() - 1); // zap last &
        }
        return new URI(getScheme(), getUserInfo(), getHost(), getPort(), getPath(), newQuery.toString(), getFragment());
    }

    String host = IRIS_HOST;

    int port = 80;

    public void setBaseURL(URI uri) {
        internalSetBaseURI(uri);
    }
    
    public int nodata = 204;


    String scheme = "http";
    
    String fdsnQueryStyle = "query";
    
    String fdsnwsPath = "fdsnws";

    HashMap<String, String> params = new HashMap<String, String>();

    void cloneNonParams(AbstractQueryParams other) {
        host = other.getHost();
        port = other.getPort();
        scheme = other.getScheme();
        fdsnQueryStyle = other.getFdsnQueryStyle();
        fdsnwsPath = other.getFdsnwsPath();
    }
    
    void internalSetBaseURI(URI baseURI) {
        setScheme(baseURI.getScheme());
        this.port = baseURI.getPort();
        setFdsnwsPath(baseURI.getPath());
        this.host = baseURI.getHost();
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    /** sets the scheme, usually to either http or https. If the scheme is https and the current port
     * is 80, the port is reset to 443 and vice versa.
     * @param scheme either http or https
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
        if (scheme.equalsIgnoreCase( "https") && getPort() == 80) {
            port = 443;
        } else if (scheme.equalsIgnoreCase("http") && getPort() == 443) {
            port = 80;
        }
    }

    public void useHTTPS() {
        setScheme("https");
    }

    public void useHTTP() {
        setScheme("http");
    }

    public String getScheme() {
        return scheme;
    }

    public int getPort() {
        return port;
    }

    String getUserInfo() {
        return null;
    }

    String getFragment() {
        return null;
    }

    String getPath() {
        return "/"+getFdsnwsPath()+"/" + getServiceName() + "/" + getFDSNMajorVersion() + "/"+getFdsnQueryStyle();
    }
    
    
    public String getFdsnwsPath() {
        return fdsnwsPath;
    }

    
    public void setFdsnwsPath(String fdsnwsPath) {
        this.fdsnwsPath = fdsnwsPath;
    }

    public String getFdsnQueryStyle() {
        return fdsnQueryStyle;
    }

    public void setFdsnQueryStyle(String queryStyle) {
        fdsnQueryStyle = queryStyle;
    }

    public String getFDSNMajorVersion() {
        return "1";
    }

    /** Service name as defined by the fdsn, ie event, station or dataselect. */
    public abstract String getServiceName();

    public String getHost() {
        return host;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public static final String NO_DATA = "nodata";

    public static final String IRIS_HOST = "service.iris.edu";
    public static final String EARTHSCOPE_HOST = "service.earthscope.org";
    
    // actual used is per service, event is usgs, station and datacenter are iris
    public static final String DEFAULT_HOST = EARTHSCOPE_HOST;

    public static final String FORMAT = "format";

    public static final String NEWLINE = "\n";
}
