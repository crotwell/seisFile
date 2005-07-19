package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.IOException;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * PSNEventInfo.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNEventInfo {
    private DataInputStream dis;
    private byte[] fourBytes = new byte[4];
    private byte[] sixBytes = new byte[6];

    /**the important variables**/
    private PSNDateTime time;
    private double lat, lon, depthKM;
    private double[] magnitudes;
    private String otherMagType;
    private byte eventType, locationQuality; //a quarlity byte, this is
    private short flags;
    private String reportingAgency;

    public PSNEventInfo(DataInputStream data) throws IOException{
        dis = data;

        time = new PSNDateTime(dis);
        //System.out.println(time.toString());
        lat = SacTimeSeries.swapBytes(dis.readDouble());
        lon = SacTimeSeries.swapBytes(dis.readDouble());
        depthKM = SacTimeSeries.swapBytes(dis.readDouble());

        magnitudes = new double[6];
        for (int i = 0; i < magnitudes.length; i++) {
            magnitudes[i] = (double)SacTimeSeries.swapBytes(dis.readShort())/100.0;
        }

        dis.readFully(fourBytes);
        otherMagType = new String(PSNDataFile.chopToLength(fourBytes));

        eventType = dis.readByte();

        locationQuality =  dis.readByte();
        flags = SacTimeSeries.swapBytes((short)dis.readUnsignedShort());

        dis.readFully(sixBytes);
        reportingAgency = new String(PSNDataFile.chopToLength(sixBytes));
    }

    public PSNDateTime getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getDepthKM() {
        return depthKM;
    }

    public double[] getMagnitudes() {
        return magnitudes;
    }

    public String getOtherMagType() {
        return otherMagType;
    }

    public byte getEventType(){
        return eventType;
    }

    public byte getLocationQuality() {
        return locationQuality;
    }

    public short getFlags() {
        return flags;
    }

    public String getReportingAgency() {
        return reportingAgency;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("Time: " + time.toString() + '\n');
        buf.append("Location: Lat " + lat + ", Lon " + lon + ", Depth " + depthKM + '\n');

        buf.append("Magnitudes: ");
        for (int i = 0; i < magnitudes.length; i++) {
            buf.append(magnitudes[i] + " ");
        }
        buf.append('\n');
        buf.append("Other Mag type: " + otherMagType + '\n');

        buf.append("Event Type Code: " + eventType + '\n');
        buf.append("Event Location Quarlity: " + locationQuality + '\n');
        buf.append("Flags: " + flags + '\n');
        buf.append("Reporting Agency: " + reportingAgency + '\n');

        return buf.toString();
    }

}

