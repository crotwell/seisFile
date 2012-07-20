package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.sac.SacHeader;

/**
 * PSNPolesAndZeros.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNPolesAndZeros{
    private DataInputStream dis;

    /**The important stuff**/
    short numZeros, numPoles;
    double[][] zeros, poles;

    public PSNPolesAndZeros(DataInputStream data) throws IOException{
        dis = data;

        numZeros = SacHeader.swapBytes(dis.readShort());
        numPoles = SacHeader.swapBytes(dis.readShort());

        zeros = new double[numZeros][2];
        for (int i = 0; i < numZeros; i++) {
            for (int j = 0; j < 2; j++) {
                zeros[i][j] = Double.longBitsToDouble(SacHeader.swapBytes(dis.readLong()));
            }
        }

        poles = new double[numPoles][2];
        for (int i = 0; i < numZeros; i++) {
            for (int j = 0; j < 2; j++) {
                poles[i][j] = Double.longBitsToDouble(SacHeader.swapBytes(dis.readLong()));
            }
        }
    }

    public short getNumZeros() {
        return numZeros;
    }

    public short getNumPoles() {
        return numPoles;
    }

    public double[][] getZeros() {
        return zeros;
    }

    public double[][] getPoles() {
        return poles;
    }
}

