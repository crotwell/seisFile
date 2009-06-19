package edu.sc.seis.seisFile.sac;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

/**
 *
 * @author viglione
 */
public class TestSacFileData extends TestCase
{
    // A tolerance value to check floating-point values.
    public static final double TOL = 0.000001;
    // This array holds the expected values of the .sac file based on the
    // graphical representation of the data.
    public static double[] data = {1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, 1.0,
                                    1.0, 1.0, 1.0, 2.0, 7.0, 13.0, 18.0, 10.0, -4.0, -16.0};

    /** @Test */
    public void testSacTimeSeries() throws IOException
    {
        /*
         * This snippet of code uses the SacTimeSeries to pull all the data out
         * of a .sac file and print it.  Each data point is a magnitude, and is
         * saved in an array made by the SacTimeSeries.
         */
        SacTimeSeries sts = new SacTimeSeries();

        sts.read(new DataInputStream(TestSacFileData.class.getClassLoader().getResourceAsStream("edu/sc/seis/seisFile/sac/control.sac")));

        assertEquals(20, sts.npts);

        for (int i = 0; i < sts.npts; i++)
        {
            assertEquals(data[i], sts.y[i], TOL);
        }
    }

    /** @Test */
    public void testByteSkip() throws FileNotFoundException, IOException
    {
        /*
         * This snippet of code uses a DataInputStream, which takes a FileInputStream,
         * which takes the .sac file as a parameter.  I skip through the header to the
         * integer representing the number of data points in the .sac file, then skip
         * the rest of the header to get to the data.
         */
        DataInputStream sample = new DataInputStream(TestSacFileData.class.getClassLoader().getResourceAsStream("edu/sc/seis/seisFile/sac/control.sac"));

        // Skip 316 bytes of the header to the number of available points
        sample.skip(316);
        int avail = sample.readInt();

        // This test will pass, since there are 20 data points in the .sac file.
        assertEquals(20, avail);

        // Skip the rest of the header to get to the data set
        sample.skip(312);

        /*
         * This loop will iterate over the upcoming float values in the .sac file,
         * and check them against the expected values based on the graph.  This test
         * will pass since the final data point is read correctly.
         */
        for (int i = 0; i < avail; i++)
        {
            assertEquals(data[i], sample.readFloat(), TOL);
        }

    }
}
