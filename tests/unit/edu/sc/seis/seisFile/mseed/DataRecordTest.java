/**
 * DataRecordTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.fissuresUtil.mseed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;
import edu.iris.Fissures.model.ISOTime;
import edu.iris.dmc.seedcodec.Steim1;
import edu.iris.dmc.seedcodec.SteimFrameBlock;

public class DataRecordTest extends TestCase {

    public DataRecordTest(String name) {
        super(name);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWrite() throws Exception {
        DataHeader header = new DataHeader(1,
                                           'D',
                                           false);
        header.setChannelIdentifier("BHZ");
        short dataOffset = (short)(48+8);
        header.setDataOffset(dataOffset);
        short blockOffset = (short)48;
        header.setDataBlocketteOffset(blockOffset);
        header.setLocationIdentifier("00");
        header.setNetworkCode("SP");
        byte numBlockettes = (byte)1;
        header.setNumBlockettes(numBlockettes);
        short numSamp = (short)10;
        header.setNumSamples(numSamp);
        short sampFac = (short)20;
        header.setSampleRateFactor(sampFac);
        short sampMul = (short)-100;
        header.setSampleRateMultiplier(sampMul);
        int year = 2001;
        int jday = 251;
        int hour = 13;
        int min = 23;
        int sec = 56;
        int tsec = 9870; // ISO time doesn't use tenthmillis, last is 0
        //System.out.println(""+year+jday+"J"+hour+min+sec+"."+tsec+"Z");
        ISOTime iso = new ISOTime(""+year+jday+"J"+hour+min+sec+"."+tsec+"Z");
        header.setStartTime(iso.getDate());
        header.setStationIdentifier("ACFLR");

        Blockette1000 blockette = new Blockette1000();
        byte encoding = (byte)10;
        byte wordOrder = (byte)1;
        byte dataLength = (byte)12;
        byte reserved = (byte)0;
        short nextOffset = zero;
        blockette.setEncodingFormat(encoding);
        blockette.setWordOrder(wordOrder);
        blockette.setDataRecordLength(dataLength);
        blockette.setReserved(reserved);

        int[] intData = new int[numSamp];
        for (int i = 0; i < intData.length; i++) {
            intData[i] = i;
        }
        SteimFrameBlock block = Steim1.encode(intData, 63);
        byte[] data = block.getEncodedData();

        DataRecord record = new DataRecord(header);
        record.addBlockette(blockette);
        record.setData(data);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        record.write(new DataOutputStream(bos));

        byte[] out = bos.toByteArray();

        File tmpMSeed = File.createTempFile("junit_fissuresUtil", ".mseed");
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(tmpMSeed));
        record.write(fos);

        //        for (int i = 0; i < 20; i++) {
        //            System.out.print(out[i]);
        //        }
        //        System.out.println("");
        assertEquals("record length", 4096, out.length);
        DataRecord outRec =  DataRecord.read(new DataInputStream(new ByteArrayInputStream(out)));
        DataHeader outHeader = outRec.getHeader();


        assertEquals("year=", year, Utility.bytesToShort(out[20], out[21], false));
        assertEquals("jday=", jday, Utility.bytesToShort(out[22], out[23], false));
        assertEquals("hour=", hour, out[24]);
        assertEquals("min=", min, out[25]);
        assertEquals("sec=", sec, out[26]);
        assertEquals("unused=", 0, out[27]);
        assertEquals("tsec=", tsec, Utility.bytesToShort(out[28], out[29], false));
        assertEquals("numPTS=", numSamp, Utility.bytesToShort(out[30], out[31], false));
        assertEquals("sampFac=", sampFac, Utility.bytesToShort(out[32], out[33], false));
        assertEquals("sampMul=", sampMul, Utility.bytesToShort(out[34], out[35], false));
        assertEquals("ac=", zero, out[36]);
        assertEquals("io=", zero, out[37]);
        assertEquals("qual=", zero, out[38]);
        assertEquals("numBlockettes=", numBlockettes, out[39]);
        assertEquals("ac=", zero, out[40]);
        assertEquals("ac=", zero, out[41]);
        assertEquals("ac=", zero, out[42]);
        assertEquals("ac=", zero, out[43]);
        assertEquals("beginData=",header.getDataOffset(),Utility.bytesToShort(out[44], out[45], false));
        assertEquals("firstBlockette=", blockOffset,Utility.bytesToShort(out[46], out[47], false));


        assertEquals("type", (short)blockette.getType(), Utility.bytesToShort(out[48], out[49], false));
        assertEquals("offset", nextOffset, Utility.bytesToShort(out[50], out[51], false));

        assertEquals("encoding", encoding, out[52]);
        assertEquals("wordOrder", wordOrder, out[53]);
        assertEquals("dataLength", dataLength, out[54]);
        assertEquals("reserved", reserved, out[55]);

        for (int j = 56; j < 64; j++) {
            assertEquals("zero pad "+j, zero, out[j]);
        }
        byte[] outData = new byte[64*63];
        System.arraycopy(out, 64, outData, 0, 63*64);
        int[] outIntData = Steim1.decode(outData, numSamp, false);
        assertEquals("data length", intData.length, outIntData.length);
        for (int i = 0; i < outIntData.length; i++) {
            assertEquals("data "+i, intData[i], outIntData[i]);
        }

    }

    static final byte zero = (byte)0;
}

