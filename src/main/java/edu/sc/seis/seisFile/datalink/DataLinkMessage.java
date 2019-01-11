package edu.sc.seis.seisFile.datalink;

public class DataLinkMessage extends DataLinkResponse {

    public DataLinkMessage(DataLinkHeader header, String message) {
        super(header);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return super.getHeaderString()+" | "+message;
    }
    
    private String message;
}
