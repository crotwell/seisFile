package edu.sc.seis.seisFile.datalink;

public class DataLinkMessage extends DataLinkResponse {

    public DataLinkMessage(DataLinkHeader header, String message) {
        super(header);
        this.message = message;
    }

    String message;
}
