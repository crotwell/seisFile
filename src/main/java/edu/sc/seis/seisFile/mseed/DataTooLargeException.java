package edu.sc.seis.seisFile.mseed;


public class DataTooLargeException extends SeedFormatException {

    public DataTooLargeException() {
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(String s) {
        super(s);
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(String s, Throwable cause) {
        super(s, cause);
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(String s, DataHeader header) {
        super(s, header);
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(Throwable cause, DataHeader header) {
        super(cause, header);
        // TODO Auto-generated constructor stub
    }

    public DataTooLargeException(String s, Throwable cause, DataHeader header) {
        super(s, cause, header);
        // TODO Auto-generated constructor stub
    }
}
