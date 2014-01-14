package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;

import edu.sc.seis.seisFile.SeisFileException;

public class FDSNWSException extends SeisFileException {

    public FDSNWSException() {}

    public FDSNWSException(String message) {
        super(message);
    }

    public FDSNWSException(Throwable cause) {
        super(cause);
    }

    public FDSNWSException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FDSNWSException(String message, URI targetURI) {
        this(message);
        this.targetURI = targetURI;
    }
    
    public FDSNWSException(String message, URI targetURI, int httpResponseCode) {
        this(message);
        this.targetURI = targetURI;
        this.httpResponseCode = httpResponseCode;
    }

    public FDSNWSException(Throwable cause, URI targetURI) {
        this(cause);
        this.targetURI = targetURI;
    }

    public FDSNWSException(String message, Throwable cause, URI targetURI) {
        this(message, cause);
        this.targetURI = targetURI;
    }

    URI targetURI;
    
    int httpResponseCode = 0;

    public URI getTargetURI() {
        return targetURI;
    }

    public void setTargetURI(URI uri) {
        this.targetURI = uri;
    }

    
    public int getHttpResponseCode() {
        return httpResponseCode;
    }
    
    public String toString() {
        return super.toString()+" uri: "+getTargetURI()+" ret code"+getHttpResponseCode();
    }
    
}
