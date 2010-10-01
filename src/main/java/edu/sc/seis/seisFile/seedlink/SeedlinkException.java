package edu.sc.seis.seisFile.seedlink;

public class SeedlinkException extends Exception {

    public SeedlinkException() {}

    public SeedlinkException(String message) {
        super(message);
    }

    public SeedlinkException(Throwable cause) {
        super(cause);
    }

    public SeedlinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
