package edu.sc.seis.seisFile.mseed;


public class DataTooLargeException extends SeedFormatException {

    public DataTooLargeException() {
    }

    public DataTooLargeException(String s) {
        super(s);
    }

    public DataTooLargeException(Throwable cause) {
        super(cause);
    }

    public DataTooLargeException(String s, Throwable cause) {
        super(s, cause);
    }

    public DataTooLargeException(String s, DataHeader header) {
        super(s, header);
    }

    public DataTooLargeException(Throwable cause, DataHeader header) {
        super(cause, header);
    }

    public DataTooLargeException(String s, Throwable cause, DataHeader header) {
        super(s, cause, header);
    }
}
