# Introduction #

Miniseed support is good for straight miniseed, ie only binary "data records" and no ascii "control records". It is less complete for the control blockettes in full SEED. SeisFile also does not include routines to decompress seed data directly, please see the [SeedCodec project](http://code.google.com/p/seedcodec/) for these routines.

# Example #

## Reading ##

A miniseed file can be read like this.

```
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
```

Here is an example of combining miniseed data records into a single float array using the [/p/seedcodec seedcodec] package to do the decompression. Given most seismic data is integral counts, you might choose an
int array instead. Obviously this is the bare minimum as you would
want to do checks to make sure all data records were from the same
channel, that there were no gaps or overlaps, sampling rate is the
same, data type is convertible to what you want, etc. Also, as shown
in the code, if the "miniseed" is not really "miniseed" becuase it
does not have a blockette1000, then you are hosed. Happens more often
than you might think!

```
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
```

## Writing ##

An example of progromatically creating miniseed records and writing them out is in the [examples](https://code.google.com/p/seisfile/source/browse/src/example/java/edu/sc/seis/seisFile/example/WriteMiniSeed.java).