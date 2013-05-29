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

    public FDSNWSException(Throwable cause, URI targetURI) {
        this(cause);
        this.targetURI = targetURI;
    }

    public FDSNWSException(String message, Throwable cause, URI targetURI) {
        this(message, cause);
        this.targetURI = targetURI;
    }

    URI targetURI;

    public URI getTargetURI() {
        return targetURI;
    }

    public void setTargetURI(URI uri) {
        this.targetURI = uri;
    }
    
}
