package edu.sc.seis.seisFile.gcf;


public class GCFFormatException extends Exception {

    public GCFFormatException() {
    }

    public GCFFormatException(String message) {
        super(message);
    }

    public GCFFormatException(Throwable t) {
        super(t);
    }

    public GCFFormatException(String message, Throwable t) {
        super(message, t);
    }
}
