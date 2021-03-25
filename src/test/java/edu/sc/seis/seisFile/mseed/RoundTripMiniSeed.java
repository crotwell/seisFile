package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.time.Instant;


import edu.iris.dmc.seedcodec.B1000Types;
import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.Steim2;
import edu.iris.dmc.seedcodec.SteimFrameBlock;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;


public class RoundTripMiniSeed {

    public DataRecord createRecord(int[] data) throws SeedFormatException, IOException, UnsupportedCompressionType, CodecException {

        int seq = 1;
        byte seed4096 = (byte)12;
        byte seed512 = (byte)9;


        DataHeader header = new DataHeader(seq++, 'D', false);
        header.setStationIdentifier("FAKE");
        header.setChannelIdentifier("BHZ");
        header.setNetworkCode("XX");
        header.setLocationIdentifier("00");
        header.setNumSamples((short)data.length);
        header.setSampleRate(.05f);
        Btime btime = new Btime( Instant.now());
        header.setStartBtime(btime);

        DataRecord record = new DataRecord(header);
        Blockette1000 blockette1000 = new Blockette1000();
        blockette1000.setEncodingFormat((byte)B1000Types.STEIM2);
        blockette1000.setWordOrder((byte)1);
        blockette1000.setDataRecordLength(seed4096);
        record.addBlockette(blockette1000);
        SteimFrameBlock steimData = null;

        steimData = Steim2.encode(data, 63);
        assertTrue(steimData.getNumSamples() <= data.length, "Can't fit all data into one record"+steimData.getNumSamples()+" out of "+data.length);


        assertTrue(record.isDecompressable());
        int[] out = Steim2.decode(steimData.getEncodedData(), data.length, false);
        assertArrayEquals(data, out);

        record.setData(steimData.getEncodedData());
        header.setNumSamples((short)steimData.getNumSamples());


        Codec codec = new Codec();
        DecompressedData decomp = codec.decompress(B1000Types.STEIM2,
                                steimData.getEncodedData(),
                                data.length,
                                false);
        assertArrayEquals(data, decomp.getAsInt());

        return record;
    }

    @Test
    public void test() throws Exception {
        int[] data = new int[512];
        // make some fake data, use sqrt so more data will be "small"
        for (int i = 0; i < data.length; i++) {
            data[i] = (int)(Math.round(Math.sqrt(Math.random())*2000)) * (Math.random() > 0.5? 1 : -1);
        }
        DataRecord dr = createRecord(data);
        DecompressedData decompData = dr.decompress();
        int[] temp = decompData.getAsInt();
        assertArrayEquals(data, temp);

    }
}
