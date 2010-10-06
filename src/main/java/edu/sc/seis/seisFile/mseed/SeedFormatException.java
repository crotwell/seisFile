
package edu.sc.seis.seisFile.mseed;

import edu.sc.seis.seisFile.SeisFileException;


public class SeedFormatException extends SeisFileException {

    public SeedFormatException() {
        super();
    }
    
    public SeedFormatException(String s) {
        super(s);
    }

    public SeedFormatException(Throwable cause) {
        super(cause);
    }
    
    public SeedFormatException(String s, Throwable cause) {
        super(s, cause);
    }
}

