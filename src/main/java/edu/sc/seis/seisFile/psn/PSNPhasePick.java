package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * PSNPhasePick.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNPhasePick{
    private DataInputStream dis;
    private byte[] eightBytes = new byte[8];
    private byte[] sixteenBytes = new byte[16];

    /**The important data**/
    private PSNDateTime startTime;
    private String phase;
    private short flags;
    private short dispYPosition;
    private String travelTimeFileName;
    private short tableDepth;

    public PSNPhasePick(DataInputStream data) throws IOException{
        dis = data;

        startTime = new PSNDateTime(dis);

        dis.readFully(eightBytes);
        phase = new String(PSNDataFile.chopToLength(eightBytes));

        flags = SacTimeSeries.swapBytes((short)dis.readUnsignedShort());
        dispYPosition = SacTimeSeries.swapBytes(dis.readShort());

        dis.readFully(sixteenBytes);
        travelTimeFileName = new String(PSNDataFile.chopToLength(sixteenBytes));

        tableDepth = SacTimeSeries.swapBytes(dis.readShort());
    }

    public PSNDateTime getStartTime() {
        return startTime;
    }

    public String getPhase() {
        return phase;
    }

    public short getFlags() {
        return flags;
    }

    public short getDispYPosition() {
        return dispYPosition;
    }

    public String getTravelTimeFileName() {
        return travelTimeFileName;
    }

    public short getTableDepth() {
        return tableDepth;
    }
}

