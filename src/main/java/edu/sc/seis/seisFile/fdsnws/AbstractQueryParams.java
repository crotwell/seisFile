package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class AbstractQueryParams {

    public AbstractQueryParams(URI baseURI) {
        this.baseURI = baseURI;
    }

    void setParam(String key, String value) {
        params.put(key, value);
    }

    void appendToParam(String key, String value) {
        if (params.containsKey(key)) {
            value = params.get(key) + "," + value;
        }
        params.put(key, params.get(key) + "," + value);
    }
    
    void removeParam(String key) {
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
