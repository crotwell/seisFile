package edu.sc.seis.seisFile.datalink;

public class DataLinkException extends Exception {

    public DataLinkException() {
        super();
    }

    public DataLinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataLinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLinkException(String message) {
        super(message);
    }

    public DataLinkException(Throwable cause) {
        super(cause);
    }

}
