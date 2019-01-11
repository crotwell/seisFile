package edu.sc.seis.seisFile.datalink;

public abstract class DataLinkResponse {

    public DataLinkResponse(DataLinkHeader header) {
        this.header = header;
    }
    
    public DataLinkHeader getHeader() {
        return header;
    }

    /** pass through getter to header */
    public String getHeaderString() {
        return header.getHeaderString();
    }
    /** pass through getter to header */
    public String headerSplit(int index) {
        return header.headerSplit(index);
    }
    /** pass through getter to header */
    public String getKey() {
        return header.getKey();
    }
    /** pass through getter to header */
    public int getDataSize() {
        return header.getDataSize();
    }
    
    private DataLinkHeader header;
}
