package edu.sc.seis.seisFile.fdsnws.dataSelectWS;

import edu.sc.seis.seisFile.SeisFileException;


public class DataSelectException extends SeisFileException {

    public DataSelectException() {
    }

    public DataSelectException(String message) {
        super(message);
    }

    public DataSelectException(Throwable cause) {
        super(cause);
    }

    public DataSelectException(String message, Throwable cause) {
        super(message, cause);
    }
}
