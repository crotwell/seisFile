
package edu.sc.seis.seisFile.mseed;

import edu.sc.seis.seisFile.SeisFileException;


public class SeedFormatException extends SeisFileException {

    public SeedFormatException() {
        super();
    }

    public SeedFormatException(ControlHeader header) {
        super();
        this.header = header;
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
    
    public SeedFormatException(String s, ControlHeader header) {
        super(s);
        this.header = header;
    }

    public SeedFormatException(Throwable cause, ControlHeader header) {
        super(cause);
        this.header = header;
    }
    
    public SeedFormatException(String s, Throwable cause, ControlHeader header) {
        super(s, cause);
        this.header = header;
    }
    
    
    public ControlHeader getHeader() {
        return header;
    }

    
    public void setHeader(ControlHeader header) {
        this.header = header;
    }

    public String toString() {
        if (header != null) {
            return super.toString()+header.toString();
        }
        return super.toString();
    }
    
    private ControlHeader header;
}

