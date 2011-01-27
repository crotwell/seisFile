package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * PSNEventRecord.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNEventRecord {
    private DataInputStream dis;
    private PSNHeader fixedHeader;
    private PSNVariableHeader varHeader;

    private boolean isShort = false, isInt = false, isFloat = false, isDouble = false;
    private short[] samplesShort;
    private int[] samplesInt;
    private float[] samplesFloat;
    private double[] samplesDouble;

    public PSNEventRecord(DataInputStream data) throws IOException{
        this(new PSNHeader(data), data);
    }

    public PSNEventRecord(PSNHeader header, DataInputStream data) throws IOException{
        dis = data;
        fixedHeader = header;
        varHeader = new PSNVariableHeader(dis, (int)header.getVarHeadLength());
        readSampleData();
        if(fixedHeader.getFlags() == 1){
            if(dis.readShort() != 0){
                throw new IOException("CRC-16 check has wrong value!");
            }
        }
        else{
            dis.skipBytes(2);
        }
    }

    private void readSampleData() throws IOException{
        byte dataType = fixedHeader.getSampleDataType();

        switch (dataType){
            case 0:
                isShort = true;
                samplesShort = new short[fixedHeader.getSampleCount()];
                for (int i = 0; i < samplesShort.length; i++) {
                    samplesShort[i] = SacTimeSeries.swapBytes(dis.readShort());
                }
                break;
            case 1:
                isInt = true;
                samplesInt = new int[fixedHeader.getSampleCount()];
                for (int i = 0; i < samplesInt.length; i++) {
                    samplesInt[i] = SacTimeSeries.swapBytes(dis.readInt());
                }
                break;
            case 2:
                isFloat = true;
                samplesFloat = new float[fixedHeader.getSampleCount()];
                for (int i = 0; i < samplesFloat.length; i++) {
                    // careful here to swap on int then make it a float
                    // byte swapping a float is dangerous as a bit pattern that is
                    // NaN will not always remain the same on conversion to/from a float
                    // see javadocs for intBitsToFloat
                    samplesFloat[i] = Float.intBitsToFloat(SacTimeSeries.swapBytes(dis.readInt()));
                }
                break;
            case 3:
                isDouble = true;
                samplesDouble = new double[fixedHeader.getSampleCount()];
                for (int i = 0; i < samplesDouble.length; i++) {
                    samplesDouble[i] = Double.longBitsToDouble(SacTimeSeries.swapBytes(dis.readLong()));
                }
                break;
            default:
                throw new IOException("data type not supported");
        }
    }

    public PSNHeader getFixedHeader(){
        return fixedHeader;
    }

    public PSNVariableHeader getVariableHeader(){
        return varHeader;
    }

    public boolean isSampleDataShort(){
        return isShort;
    }

    public short[] getSampleDataShort(){
        return samplesShort;
    }

    public boolean isSampleDataInt(){
        return isInt;
    }

    public int[] getSampleDataInt(){
        return samplesInt;
    }

    public boolean isSampleDataFloat(){
        return isFloat;
    }

    public float[] getSampleDataFloat(){
        return samplesFloat;
    }

    public boolean isSampleDataDouble(){
        return isDouble;
    }

    public double[] getSampleDataDouble(){
        return samplesDouble;
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

        buf.append(fixedHeader.toString());
        buf.append(varHeader.toString());

        if (isShort){
            for (int i = 0; i < samplesShort.length; i++) {
                buf.append(samplesShort[i] + " ");
            }
        }
        if (isInt){
            for (int i = 0; i < samplesInt.length; i++) {
                buf.append(samplesInt[i] + " ");
            }
        }
        if (isFloat){
            for (int i = 0; i < samplesFloat.length; i++) {
                buf.append(samplesFloat[i] + " ");
            }
        }
        if (isDouble){
            for (int i = 0; i < samplesDouble.length; i++) {
                buf.append(samplesDouble[i] + " ");
            }
        }
        buf.append('\n');

        return buf.toString();
    }

}

