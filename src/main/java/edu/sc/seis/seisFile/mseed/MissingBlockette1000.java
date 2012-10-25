package edu.sc.seis.seisFile.mseed;

public class MissingBlockette1000 extends SeedFormatException {


    public MissingBlockette1000(ControlHeader header) {
        super(header);
    }

    public MissingBlockette1000(String s, ControlHeader header) {
        super(s, header);
    }
    
    public MissingBlockette1000(Throwable cause, ControlHeader header) {
        super(cause, header);
    }

    public MissingBlockette1000(String s, Throwable cause, ControlHeader header) {
        super(s, cause, header);
    }
}
