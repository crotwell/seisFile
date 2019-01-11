package edu.sc.seis.seisFile.datalink;

public class DataLinkHeader {

    public DataLinkHeader(String headerString) {
        this.headerString = headerString;
        this.headerSplit = headerString.split(" ");
        this.key = headerSplit[0];
        if (key.equals("INFO") || key.equals("PACKET") || key.equals("OK") || key.equals("ERROR")) {
            this.dataSize = Integer.parseInt(headerSplit[headerSplit.length-1]);
        }
    }
    
    public String getHeaderString() {
        return headerString;
    }
    public String headerSplit(int index) {
        return headerSplit[index];
    }
    public String getKey() {
        return key;
    }
    public int getDataSize() {
        return dataSize;
    }
    public boolean isMessageType() {
        return getKey().startsWith(DataLink.ID) || 
                key.startsWith(DataLink.INFO) || 
                key.startsWith(DataLink.OK) || 
                key.startsWith(DataLink.ERROR) || 
                key.startsWith(DataLink.ENDSTREAM);
    }

    public String toString() {
        return headerString;
    }
    
    private String headerString;
    private String[] headerSplit;
    private String key;
    private int dataSize = 0;
}
