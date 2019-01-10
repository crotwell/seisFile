package edu.sc.seis.seisFile.datalink;

public abstract class DataLinkResponse {

    public DataLinkResponse(DataLinkHeader header) {
        this.header = header;
    }
    
    DataLinkHeader header;
}
