package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * PSNHeader.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNHeader {
    private DataInputStream dis;
    private byte[] eightBytes = new byte[8];
    private byte[] sixBytes = new byte[6];
    private byte[] fourBytes = new byte[4];
    private byte[] threeBytes = new byte[3];
    private byte[] twoBytes = new byte[2];
    private boolean isVolume = false;
    private int numRecords;

    /**the important variables**/
    private int varHeadLength;
    private PSNDateTime dateTime;
    private double startTimeOffset, sampleRate;
    private int sampleCount, flags;
    private String timeRefType;
    private byte timeRefStatus, sampleDataType, sampleCompression;
    private double compIncident, compAz;
    private byte compOrientation, sensorType;
    private double sensorLat, sensorLong, sensorElevation;
    private String sensorName, channelId, sensorNetwork;
    private double sensitivity, magCorrect;
    private short adBitRes;
    private double sampleMin, sampleMax, sampleMean;

    public PSNHeader(DataInputStream data) throws IOException, FileNotFoundException{
        dis = data;

        /**FileID and Version**/
        dis.readFully(eightBytes);
        String fileFormat = new String(eightBytes);
        //System.out.println("File Format: " + fileFormat);
        if (fileFormat.equals("PSNVOLUM")){
            dis.readFully(twoBytes);
            if ((new String(twoBytes)).equals("E1")){
                isVolume = true;
                numRecords = SacTimeSeries.swapBytes(dis.readShort());
                //System.out.println("Number of records: " + numRecords);
            }
            else throw new FileNotFoundException("File is not of type PSNVOLUME1");
        }
        else if (!fileFormat.equals("PSNTYPE4")){
            //System.out.println(fileFormat);
            throw new FileNotFoundException("File is not of type PSNTYPE4: " + fileFormat);
        }

        if (!isVolume){
            numRecords = 1;
            readHeader();
        }
    }

    public boolean isVolumeFile(){
        return isVolume;
    }

    public int getNumRecords(){
        return numRecords;
    }

    private void readHeader() throws IOException{
        /**Variable Header Length**/
        //varHeadLength = dis.readInt();
        varHeadLength = SacTimeSeries.swapBytes(dis.readInt());
        //System.out.println("varHeadLength = " + Integer.toHexString(varHeadLength)  + " or " + varHeadLength);

        dateTime = new PSNDateTime(dis);

        /**StartTime offset**/
        startTimeOffset = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**SampleRate**/
        sampleRate = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**SampleCount**/
        sampleCount = SacTimeSeries.swapBytes(dis.readInt());

        /**Flags**/
        flags = SacTimeSeries.swapBytes(dis.readInt());

        /**Timing Ref Type**/
        dis.readFully(threeBytes);
        timeRefType = new String(PSNDataFile.chopToLength(threeBytes));

        /**Timing Ref Status**/
        timeRefStatus = dis.readByte();

        /**Sample Data Type**/
        sampleDataType = dis.readByte();

        /**Sample Compression**/
        sampleCompression = dis.readByte();

        /**Component Incident**/
        compIncident = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Component Azimuth**/
        compAz = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Component Orientation**/
        compOrientation = dis.readByte();

        /**Sensor Type**/
        sensorType = dis.readByte();

        /**Sensor Latitude**/
        sensorLat = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Sensor Longitude**/
        sensorLong = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Sensor Elevation**/
        sensorElevation = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Sensor Name**/
        dis.readFully(sixBytes);
        sensorName = new String(PSNDataFile.chopToLength(sixBytes));

        /**ChannelID**/
        dis.readFully(fourBytes);
        channelId = new String(PSNDataFile.chopToLength(fourBytes));

        /**Sensor Network**/
        dis.readFully(sixBytes);
        sensorNetwork = new String(PSNDataFile.chopToLength(sixBytes));

        /**Sensitivity**/
        sensitivity = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Magnitude Correction**/
        magCorrect = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**A/D Bit Resolution**/
        adBitRes = SacTimeSeries.swapBytes(dis.readShort());

        /**Sample Minimum**/
        sampleMin = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Sample Maximum**/
        sampleMax = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));

        /**Sample Mean**/
        sampleMean = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));
    }

    public int getVarHeadLength() {
        return varHeadLength;
    }

    public PSNDateTime getDateTime() {
        return dateTime;
    }

    public double getStartTimeOffset() {
        return startTimeOffset;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getFlags() {
        return flags;
    }

    public String getTimeRefType() {
        return timeRefType;
    }

    public byte getTimeRefStatus() {
        return timeRefStatus;
    }

    public byte getSampleDataType() {
        return sampleDataType;
    }

    public byte getSampleCompression() {
        return sampleCompression;
    }

    public double getCompIncident() {
        return compIncident;
    }

    public double getCompAz() {
        return compAz;
    }

    public byte getCompOrientation() {
        return compOrientation;
    }

    public byte getSensorType() {
        return sensorType;
    }

    public double getSensorLat() {
        return sensorLat;
    }

    public double getSensorLong() {
        return sensorLong;
    }

    public double getSensorElevation() {
        return sensorElevation;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getSensorNetwork() {
        return sensorNetwork;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public double getMagCorrect() {
        return magCorrect;
    }

    public short getAdBitRes() {
        return adBitRes;
    }

    public double getSampleMin() {
        return sampleMin;
    }

    public double getSampleMax() {
        return sampleMax;
    }

    public double getSampleMean() {
        return sampleMean;
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

        buf.append("Variable Header length: " + getVarHeadLength() + '\n');
        buf.append("Date: " + dateTime.toString() + '\n');
        buf.append("Start Time Offset: " + getStartTimeOffset() + '\n');
        buf.append("Samle Rate: " + getSampleRate() + '\n');
        buf.append("Sample Count: " + getSampleCount() + '\n');
        buf.append("Flags: " + getFlags() + '\n');
        buf.append("Timing Ref Type: " + getTimeRefType() + '\n');
        buf.append("Timing Ref Status: " + getTimeRefStatus() + '\n');
        buf.append("Sample Data Type: " + getSampleDataType() + '\n');
        buf.append("Sample Compression: " + getSampleCompression() + '\n');
        buf.append("Component Incident: " + getCompIncident() + '\n');
        buf.append("Component Azimuth: " + getCompAz() + '\n');
        buf.append("Component Orientation: " + getCompOrientation() + '\n');
        buf.append("Sensor Type: " + getSensorType() + '\n');
        buf.append("Sensor Lat/Lon: " + getSensorLat() + " " + getSensorLong() + '\n');
        buf.append("Sensor Elevation: " + getSensorElevation() + '\n');
        buf.append("Sensor Name: " + getSensorName() + '\n');
        buf.append("Channel ID: " + getChannelId() + '\n');
        buf.append("Network: " + getSensorNetwork() + '\n');
        buf.append("Sensitivity: " + getSensitivity() + '\n');
        buf.append("Magnitude Correction: " + getMagCorrect() + '\n');
        buf.append("A/D bit Resolution: " + getAdBitRes() + '\n');
        double sampMin = getSampleMin();
        double sampMax = getSampleMax();
        double sampMean = getSampleMean();
        buf.append("Min/Max/Mean: " + sampMin + " " + sampMax + " " + sampMean + '\n');

        return buf.toString();
    }

}

