package edu.sc.seis.seisFile.datalink;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class DataLinkPacket extends DataLinkResponse {

    public DataLinkPacket(DataLinkHeader header, byte[] rawData) throws DataLinkException {
        super(header);
      this.streamId = this.header.split[1];
      this.pktid = this.header.split[2];
      this.hppackettime = this.header.split[3];
      this.hppacketstart = this.header.split[4];
      this.hppacketend = this.header.split[5];
      this.dataSize = Integer.parseInt(this.header.split[6]);
      if (rawData.length < this.dataSize) {
        throw new Error("not enough bytes in raw data for packet: "+this.dataSize);
      }
      extract(rawData);
    }



    void extract(byte[] data) throws DataLinkException {
        if (this.streamId.endsWith(DataLink.MSEED_TYPE)) {
            try {
            DataInputStream bis = new DataInputStream(new ByteArrayInputStream(data));
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

    String streamId;
    String pktid;
    String hppackettime;
    String hppacketstart;
    String hppacketend;
    int dataSize;
    DataRecord miniseed;
    
}
