package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class AbstractQueryParams {

    public AbstractQueryParams(URI baseURI) {
        this.baseURI = baseURI;
    }

    protected void setParam(String key, String value) {
        params.put(key, value);
    }

    protected void setParam(String key, int value) {
        params.put(key, ""+value);
    }

    protected void setParam(String key, float value) {
        params.put(key, ""+value);
    }

    protected void setParam(String key, boolean value) {
        params.put(key, value?"true":"false");
    }

    protected void appendToParam(String key, String value) {
        if (params.containsKey(key)) {
            value = params.get(key) + "," + value;
        }
        params.put(key, params.get(key) + "," + value);
    }
    
    protected void setDateParam(String key, Date value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        setParam(key, sdf.format(value));
    }
    
    protected void removeParam(String key) {
        params.remove(key);
    }
    
    public void clear() {
        params.clear();
    }

    public URI formURI() throws URISyntaxException {
        StringBuilder newQuery = new StringBuilder(baseURI.getQuery());
        if (newQuery.length() != 0) {
            newQuery.append("&");
        }
        for (String key : params.keySet()) {
            newQuery.append(key).append("=").append(params.get(key)).append("&");
        }
        newQuery.deleteCharAt(newQuery.length() - 1); // zap last &
        return new URI(baseURI.getScheme(),
                       baseURI.getUserInfo(),
                       baseURI.getHost(),
                       baseURI.getPort(),
                       baseURI.getPath(),
                       newQuery.toString(),
                       baseURI.getFragment());
    }

    String baseURL;

    URI baseURI;

    HashMap<String, String> params = new HashMap<String, String>();
}
