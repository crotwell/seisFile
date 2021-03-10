package edu.sc.seis.seisFile.fdsnws.stationxml;

import edu.sc.seis.seisFile.SeisFileException;


public class InvalidResponse extends SeisFileException {

    public InvalidResponse() {
    }

    public InvalidResponse(String message) {
        super(message);
    }

    public InvalidResponse(Throwable cause) {
        super(cause);
    }

    public InvalidResponse(String message, Throwable cause) {
        super(message, cause);
    }
}
