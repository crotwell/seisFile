package edu.sc.seis.seisFile.example;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class MiniSeedToFloatArray {

    public List<DataRecord> readFile(String filename) throws IOException, SeedFormatException {
        List<DataRecord> drList = new ArrayList<DataRecord>();
        DataInput dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        PrintWriter out = new PrintWriter(System.out, true);
        try {
            while (true) {
                SeedRecord sr = SeedRecord.read(dis, 4096);
                // maybe print it out...
                sr.writeASCII(out);
                if (sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord)sr;
                    // now do something with the data...
                    drList.add(dr);
                }
            }
        } catch(EOFException e) {}
        return drList;
    }

    /**
     * Extracts a float array from a list of DataRecords. This assumes all of the DataRecords
     * are from the same channel, are in time order and contain no gaps or overlaps.
     * @param drList
     * @return
     * @throws UnsupportedCompressionType
     * @throws CodecException
     * @throws SeedFormatException
     */
    public float[] extract(List<DataRecord> drList) throws UnsupportedCompressionType, CodecException,
            SeedFormatException {
        int numPts = 0;
        for (DataRecord dr : drList) {
            numPts += dr.getHeader().getNumSamples();
        }
        float[] data = new float[numPts];
        int numSoFar = 0;
        for (DataRecord dr : drList) {
            DecompressedData decompData = dr.decompress();
            float[] temp = decompData.getAsFloat();
            System.arraycopy(temp, 0, data, numSoFar, temp.length);
            numSoFar += temp.length;
        }
        return data;
    }

    public static void main(String[] args) throws SeedFormatException, IOException, UnsupportedCompressionType, CodecException {
        MiniSeedToFloatArray msfa = new MiniSeedToFloatArray();
        List<DataRecord> drList = msfa.readFile(args[0]);
        float[] data = msfa.extract(drList);
        System.out.println("Found "+data.length+" floats in "+drList.size()+" data records.");
    }
}
