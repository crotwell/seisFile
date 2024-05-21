package edu.sc.seis.seisFile.datalink;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class DataLinkPacket extends DataLinkResponse {

    public DataLinkPacket(DataLinkHeader header, byte[] rawData) throws DataLinkException {
        super(header);
        this.streamId = this.headerSplit(1);
        this.pktid = this.headerSplit(2);
        this.hppackettime = this.headerSplit(3);
        this.hppacketstart = this.headerSplit(4);
        this.hppacketend = this.headerSplit(5);
        this.dataSize = Integer.parseInt(this.headerSplit(6));
        this.rawData = rawData;
        if (rawData.length < this.dataSize) {
            throw new DataLinkException("not enough bytes in raw data for packet: " + this.dataSize);
        }
    }



    public String getStreamId() {
        return streamId;
    }



    public String getPktid() {
        return pktid;
    }



    public String getHppackettime() {
        return hppackettime;
    }



    public String getHppacketstart() {
        return hppacketstart;
    }



    public String getHppacketend() {
        return hppacketend;
    }



    public int getDataSize() {
        return dataSize;
    }



    public byte[] getRawData() {
        return rawData;
    }

    public boolean isMiniseed() {
        return this.miniseed != null || this.streamId.endsWith(DataLink.MSEED_TYPE);
    }

    public DataRecord getMiniseed() throws DataLinkException {
        if (miniseed == null) {
            if (this.streamId.endsWith(DataLink.MSEED_TYPE)) {
                try {
                    DataInputStream bis = new DataInputStream(new ByteArrayInputStream(this.rawData));
                    this.miniseed = (DataRecord) DataRecord.read(bis);
                } catch(SeedFormatException e) {
                    throw new DataLinkException(e);
                } catch(IOException e) {
                    throw new DataLinkException(e);
                }
            } else {
                throw new DataLinkException("Unknown DataLink Packet type: "+this.streamId);
            }
        }
        return miniseed;
    }

    String streamId;
    String pktid;
    String hppackettime;
    String hppacketstart;
    String hppacketend;
    int dataSize;
    byte[] rawData;
    DataRecord miniseed;
    
}
