package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.SeisFileException;

public class FDSNSourceIdException extends SeisFileException {
    public FDSNSourceIdException() {}

    public FDSNSourceIdException(String message) {
        super(message);
    }

    public FDSNSourceIdException(Throwable cause) {
        super(cause);
    }

    public FDSNSourceIdException(String message, Throwable cause) {
        super(message, cause);
    }
}