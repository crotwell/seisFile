package edu.sc.seis.seisFile;

/**
 * Exception type for error in SeisFile.
 */
public class SeisFileException extends Exception {

    public SeisFileException() {
    }

    public SeisFileException(String message) {
        super(message);
    }

    public SeisFileException(Throwable cause) {
        super(cause);
    }

    public SeisFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
