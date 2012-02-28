package edu.sc.seis.seisFile.mseed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Utility.java
 * 
 * 
 * Created: Fri Apr 2 14:28:55 1999
 * 
 * @author Philip Crotwell
 */
public class Utility {
    
    public static int extractInteger(byte[] info, int start, int length) {
        return Integer.parseInt(extractString(info, start, length));
    }
    
    public static String extractString(byte[] info, int start, int length) {
        byte[] subbytes = new byte[length];
        System.arraycopy(info, start, subbytes, 0, length);
        return new String(subbytes);
    }
    
    public static String extractVarString(byte[] info, int start, int length) {
        String substring = "";
        int i=0;
        while (i<length && i<info.length && info[i] != 126) {
            substring += new String(new byte[] {info[i]});
        }
        return substring;
    }

    public static short bytesToShort(byte hi, byte low, boolean swapBytes) {
        if(swapBytes) {
            return (short)((hi & 0xff) + (low & 0xff) << 8);
        } else {
            return (short)(((hi & 0xff) << 8) + (low & 0xff));
        }
    }

    public static int bytesToInt(byte a) {
        return (int)a;
    }

    public static int uBytesToInt(byte a) {
        // we and with 0xff in order to get the sign correct (pos)
        return a & 0xff;
    }

    public static int bytesToInt(byte[] info, int i, boolean swapBytes) {
        return bytesToInt(info[i], info[i + 1], info[i + 2], info[i + 3], swapBytes);
    }

    public static int bytesToLong(byte[] info, int i, boolean swapBytes) {
        return bytesToLong(info[i], info[i + 1], info[i + 2], info[i + 3], info[i + 4], info[i + 5], info[i + 6], info[i + 7], swapBytes);
    }

    public static int bytesToInt(byte a, byte b, boolean swapBytes) {
        if(swapBytes) {
            return (a & 0xff) + ((int)b << 8);
        } else {
            return ((int)a << 8) + (b & 0xff);
        }
    }

    public static int uBytesToInt(byte a, byte b, boolean swapBytes) {
        // we "and" with 0xff to get the sign correct (pos)
        if(swapBytes) {
            return (a & 0xff) + ((b & 0xff) << 8);
        } else {
            return ((a & 0xff) << 8) + (b & 0xff);
        }
    }

    public static int bytesToInt(byte a, byte b, byte c, boolean swapBytes) {
        if(swapBytes) {
            return (a & 0xff) + ((b & 0xff) << 8) + ((int)c << 16);
        } else {
            return ((int)a << 16) + ((b & 0xff) << 8) + (c & 0xff);
        }
    }

    public static int bytesToInt(byte a,
                                 byte b,
                                 byte c,
                                 byte d,
                                 boolean swapBytes) {
        if(swapBytes) {
            return ((a & 0xff)) + ((b & 0xff) << 8) + ((c & 0xff) << 16)
                    + ((d & 0xff) << 24);
        } else {
            return ((a & 0xff) << 24) + ((b & 0xff) << 16) + ((c & 0xff) << 8)
                    + ((d & 0xff));
        }
    }

    public static int bytesToLong(byte a,
                                 byte b,
                                 byte c,
                                 byte d,
                                 byte e,
                                 byte f,
                                 byte g,
                                 byte h,
                                 boolean swapBytes) {
        if(swapBytes) {
            return ((a & 0xff)) + ((b & 0xff) << 8) + ((c & 0xff) << 16)
                    + ((d & 0xff) << 24) + ((e & 0xff) << 32) + ((f & 0xff) << 40) + ((g & 0xff) << 48)
                    + ((h & 0xff) << 56);
        } else {
            return ((a & 0xff) << 56) + ((b & 0xff) << 48) + ((c & 0xff) << 40)
                    + ((d & 0xff) << 32) + ((e & 0xff) << 24) + ((f & 0xff) << 16) + ((g & 0xff) << 8)
                    + ((h & 0xff));
        }
    }

    public static byte[] intToByteArray(int a) {
        byte[] returnByteArray = new byte[4];// int is 4 bytes
        returnByteArray[0] = (byte)((a & 0xff000000) >> 24);
        returnByteArray[1] = (byte)((a & 0x00ff0000) >> 16);
        returnByteArray[2] = (byte)((a & 0x0000ff00) >> 8);
        returnByteArray[3] = (byte)((a & 0x000000ff));
        return returnByteArray;
    }

    public static byte[] floatToByteArray(float a) {
        return intToByteArray(Float.floatToIntBits(a));
    }

    /**
     * Inserts float into dest at index pos 
     */
    public static void insertFloat(float value, byte[] dest, int pos) {
        int bits = Float.floatToIntBits(value);
        byte[] b = Utility.intToByteArray(bits);
        System.arraycopy(b, 0, dest, pos, 4);
    }

    public static byte[] pad(byte[] source, int requiredBytes, byte paddingByte) {
        if(source.length == requiredBytes) {
            return source;
        } else {
            byte[] returnByteArray = new byte[requiredBytes];
            System.arraycopy(source, 0, returnByteArray, 0, source.length);
            for(int i = source.length; i < requiredBytes; i++) {
                returnByteArray[i] = paddingByte;
            }
            return returnByteArray;
        }
    }

    public static byte[] format(byte[] source, int start, int end) {
        byte[] returnByteArray = new byte[start - end + 1];
        int j = 0;
        for(int i = start; i < end; i++, j++) {
            returnByteArray[j] = source[i];
        }
        return returnByteArray;
    }
    
    public static boolean areContiguous(DataRecord first, DataRecord second) {
        Btime fEnd = first.getHeader().getPredictedNextStartBtime();
        Btime sBegin = second.getHeader().getStartBtime();
        return fEnd.tenthMilli == sBegin.tenthMilli &&
            fEnd.sec == sBegin.sec &&
            fEnd.min == sBegin.min &&
            fEnd.hour == sBegin.hour &&
            fEnd.jday == sBegin.jday &&
            fEnd.year == sBegin.year;
    }
    
    /** breaks the List into sublists where the DataRecords are contiguous. Assumes
     * that the input List is sorted (by begin time?) and does not contain overlaps.
     */
    public static List<List<DataRecord>> breakContiguous(List<DataRecord> inList) {
        List<List<DataRecord>> out = new ArrayList<List<DataRecord>>();
        List<DataRecord> subout = new ArrayList<DataRecord>();
        DataRecord prev = null;
        for (DataRecord dataRecord : inList) {
            if (prev == null) { 
                // first one
                out.add(subout);
            } else if (areContiguous(prev, dataRecord)) {
                // contiguous
            } else {
                subout = new ArrayList<DataRecord>();
                out.add(subout);
            }
            subout.add(dataRecord);
            prev = dataRecord;
        }
        return out;
    }

    public static void main(String[] args) {
        int a = 256;
        byte a1 = (byte)((a & 0xff000000) >> 24);
        byte a2 = (byte)((a & 0x00ff0000) >> 16);
        byte a3 = (byte)((a & 0x0000ff00) >> 8);
        byte a4 = (byte)((a & 0x000000ff));
        System.out.println("first byte is " + a1);
        System.out.println("2 byte is " + a2);
        System.out.println("3 byte is " + a3);
        System.out.println("4  byte is " + a4);
        byte[] source = new byte[5];
        for(int i = 0; i < 5; i++)
            source[i] = (byte)10;
        byte[] output = Utility.pad(source, 5, (byte)32);
        // for(int j = 0; j< output.length; j++)
        // {
        // System.out.println("byte"+j+" " + output[j]);
        // }
        for(int k = output.length - 1; k > -1; k--) {
            System.out.println("byte" + k + " " + output[k]);
        }
    }

    public static void cleanDuplicatesOverlaps(List<DataRecord> drFromFileList) {
        Collections.sort(drFromFileList, new DataRecordBeginComparator());
        DataRecord prev = null;
        Iterator<DataRecord> itFromFileList = drFromFileList.iterator();
        while (itFromFileList.hasNext()) {
            DataRecord dataRecord = itFromFileList.next();
            if (prev != null && prev.getHeader().getStartBtime().equals(dataRecord.getHeader().getStartBtime())) {
                //  a duplicate
                itFromFileList.remove();
            } else if (prev != null && prev.getHeader().getLastSampleBtime().afterOrEquals(dataRecord.getHeader().getStartBtime())) {
                //  a overlap
                itFromFileList.remove();
            } else {
                prev = dataRecord;
            }
        }
    }
} // Utility
