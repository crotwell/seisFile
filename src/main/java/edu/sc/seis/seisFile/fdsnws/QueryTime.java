package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.util.Date;


public class QueryTime {
    
    public QueryTime(URI uri) {
        this.uri = uri;
        this.when = new Date();
    }
    
    public URI getURI() {
        return uri;
    }
    
    public Date getWhen() {
        return when;
    }
    
    URI uri;
    
    Date when;
}
