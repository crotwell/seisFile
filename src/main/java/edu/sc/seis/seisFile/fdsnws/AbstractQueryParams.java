package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public abstract class AbstractQueryParams {

    public AbstractQueryParams(String host) {
        this.host = host;
    }

    protected void setParam(String key, String value) {
        params.put(key, value);
    }

    protected void setParam(String key, int value) {
        params.put(key, "" + value);
    }

    protected void setParam(String key, float value) {
        params.put(key, "" + value);
    }

    protected void setParam(String key, boolean value) {
        params.put(key, value ? "true" : "false");
    }

    protected void appendToParam(String key, String value) {
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

    protected void setParam(String key, Date value) {
        SimpleDateFormat sdf = createDateFormat();
        setParam(key, sdf.format(value));
    }

    protected void clearParam(String key) {
        params.remove(key);
    }

    public void clear() {
        params.clear();
    }

    public static SimpleDateFormat createDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

    public URI formURI() throws URISyntaxException {
        StringBuilder newQuery = new StringBuilder();
        if (newQuery.length() != 0) {
            newQuery.append("&");
        }
        List<String> keyList = new ArrayList<String>();
        keyList.addAll(params.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            newQuery.append(key).append("=").append(params.get(key)).append("&");
        }
        if (newQuery.length() > 1) {
            newQuery.deleteCharAt(newQuery.length() - 1); // zap last &
        }
        return new URI(getScheme(), getUserInfo(), getHost(), getPort(), getPath(), newQuery.toString(), getFragment());
    }

    String host = IRIS_HOST;

    int port = 80;

    String scheme = "http";
    
    String fdsnQueryStyle = "query";

    HashMap<String, String> params = new HashMap<String, String>();

    void internalSetBaseURI(URI baseURI) {
        setScheme(baseURI.getScheme());
        setPort(baseURI.getPort());
        this.host = baseURI.getHost();
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    String getScheme() {
        return scheme;
    }

    int getPort() {
        return port;
    }

    String getUserInfo() {
        return null;
    }

    String getFragment() {
        return null;
    }

    String getPath() {
        return "/fdsnws/" + getServiceName() + "/" + getFDSNMajorVersion() + "/"+getFdsnQueryStyle();
    }
    
    String getFdsnQueryStyle() {
        return fdsnQueryStyle;
    }
    
    void setFdsnQueryStyle(String queryStyle) {
        fdsnQueryStyle = queryStyle;
    }

    String getFDSNMajorVersion() {
        return "1";
    }

    /** Service name as defined by the fdsn, ie event, station or dataselect. */
    public abstract String getServiceName();

    protected String getHost() {
        return host;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public static final String IRIS_HOST = "service.iris.edu";
}
