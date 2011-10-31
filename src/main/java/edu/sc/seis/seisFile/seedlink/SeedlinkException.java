package edu.sc.seis.seisFile.seedlink;

import edu.sc.seis.seisFile.SeisFileException;

public class SeedlinkException extends SeisFileException {

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
