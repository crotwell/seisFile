package edu.sc.seis.seisFile.datalink;

public class DataLinkHeader {

    public DataLinkHeader(String header) {
        this.header = header;
        this.split = header.split(" ");
        this.key = split[0];
        if (key.equals("INFO") || key.equals("PACKET") || key.equals("OK") || key.equals("ERROR")) {
            this.dataSize = Integer.parseInt(split[split.length-1]);
        }
        System.out.println("Parse header: "+key+" "+header+" "+dataSize);
    }
    
    String header;
    String[] split;
    String key;
    int dataSize = 0;
}
