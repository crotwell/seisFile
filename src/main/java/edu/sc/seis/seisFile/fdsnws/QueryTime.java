package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.time.Instant;


public class QueryTime {
    
    public QueryTime(URI uri) {
        this.uri = uri;
        this.when = Instant.now();
    }
    
    public URI getURI() {
        return uri;
    }
    
    public Instant getWhen() {
        return when;
    }
    
    URI uri;
    
    Instant when;
}
