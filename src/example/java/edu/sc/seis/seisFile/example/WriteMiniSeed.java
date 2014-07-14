package edu.sc.seis.seisFile.example;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.iris.dmc.seedcodec.Steim2;
import edu.iris.dmc.seedcodec.SteimException;
import edu.iris.dmc.seedcodec.SteimFrameBlock;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class WriteMiniSeed {

    public WriteMiniSeed() {
        // TODO Auto-generated constructor stub
    }

    public static void write() throws SeedFormatException, IOException, SteimException {
        String outFilename = "test.mseed";
        int seq = 1;
        byte seed4096 = (byte)12;
        byte seed512 = (byte)9;
        
        int[] data = new int[512];
        // make some fake data, use sqrt so more data will be "small"
        for (int i = 0; i < data.length; i++) {
            data[i] = (int)(Math.round(Math.sqrt(Math.random())*2000)) * (Math.random() > 0.5? 1 : -1);
        }

        DataHeader header = new DataHeader(seq++, 'D', false);
        header.setStationIdentifier("FAKE");
        header.setChannelIdentifier("BHZ");
        header.setNetworkCode("XX");
        header.setLocationIdentifier("00");
        header.setNumSamples((short)data.length);
        header.setSampleRate(.05f);
        Btime btime = new Btime(new Date());
        header.setStartBtime(btime);
        
        DataRecord record = new DataRecord(header);
        Blockette1000 blockette1000 = new Blockette1000();
        blockette1000.setEncodingFormat((byte)B1000Types.STEIM2);
        blockette1000.setWordOrder((byte)0);
        blockette1000.setDataRecordLength(seed4096);
        record.addBlockette(blockette1000);
        SteimFrameBlock steimData = null;
        
        steimData = Steim2.encode(data, 63);
        
        record.setData(steimData.getEncodedData());
        
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFilename)));
        record.write(out);
        out.close();
        System.out.println("Wrote miniseed to "+outFilename+", "+(data.length*4)+" compressed to "+steimData.numNonEmptyFrames()*64
                           +" record size="+record.getRecordSize());
    }
    
    public static void main(String[] args) throws SeedFormatException, SteimException, IOException {
        WriteMiniSeed.write();
    }
}
