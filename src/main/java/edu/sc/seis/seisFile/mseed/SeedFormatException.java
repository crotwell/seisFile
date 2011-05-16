
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
    
    public SeedFormatException(String s, DataHeader header) {
        super(s);
        this.header = header;
    }

    public SeedFormatException(Throwable cause, DataHeader header) {
        super(cause);
        this.header = header;
    }
    
    public SeedFormatException(String s, Throwable cause, DataHeader header) {
        super(s, cause);
        this.header = header;
    }
    
    
    public DataHeader getHeader() {
        return header;
    }

    
    public void setHeader(DataHeader header) {
        this.header = header;
    }

    private DataHeader header;
}

