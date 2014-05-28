package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;


public class FDSNWSAuthorizationException extends FDSNWSException {

    public FDSNWSAuthorizationException() {
    }

    public FDSNWSAuthorizationException(String message) {
        super(message);
    }

    public FDSNWSAuthorizationException(Throwable cause) {
        super(cause);
    }

    public FDSNWSAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FDSNWSAuthorizationException(String message, URI targetURI) {
        super(message, targetURI);
    }

    public FDSNWSAuthorizationException(String message, URI targetURI, int httpResponseCode) {
        super(message, targetURI, httpResponseCode);
    }

    public FDSNWSAuthorizationException(Throwable cause, URI targetURI) {
        super(cause, targetURI);
    }

    public FDSNWSAuthorizationException(String message, Throwable cause, URI targetURI) {
        super(message, cause, targetURI);
    }
}
