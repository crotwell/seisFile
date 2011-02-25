
package edu.sc.seis.seisFile.mseed;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class ControlHeader {

    protected int sequenceNum;
	
    protected byte typeCode;
	
    protected boolean continuationCode;
	
    public static ControlHeader read(DataInput in) 
	throws IOException, SeedFormatException {
		byte[] seqBytes = new byte[6];
		in.readFully(seqBytes);
		String seqNumString = new String(seqBytes);
	
		int sequenceNum =0;
		// check for blank string, leave as zero if so
		if ( ! seqNumString.equals("      ")) {
		    try {
		        sequenceNum = Integer.valueOf(seqNumString).intValue();
		    } catch (NumberFormatException e) {
		        System.err.println("seq num unreadable, setting to 0 "+e.toString());
		    } // end of try-catch
        }
			
		byte typeCode = in.readByte();
			
		int b = in.readByte();
		boolean continuationCode;
		if (b == 32) {
		    // a space, so no continuation
		    continuationCode = false;
		} else if (b == 42) {
		    // an asterisk, so is a continuation
		    continuationCode = true;
		} else {
		    throw new SeedFormatException("ControlHeader, expected space or *, but got "+b);
		}
			
		if (typeCode == (byte)'D' || typeCode == (byte)'R' || typeCode == (byte)'Q' || typeCode == (byte)'M') {
		    // Data Header
		    return DataHeader.read(in, sequenceNum, (char)typeCode, continuationCode);
		} else {
		    // Control header
		    return new ControlHeader(sequenceNum, typeCode, continuationCode);
		}
    }
	
    /**
     * This method writes Control Header into the output stream
     * While writing, it will conform to the format of MiniSeed
      */	
    protected void write(DataOutput dos) throws IOException {
	DecimalFormat sequenceNumFormat = new DecimalFormat("000000");
        String sequenceNumString = sequenceNumFormat.format(getSequenceNum());
	byte[] sequenceNumByteArray = null;
	try{
	    sequenceNumByteArray = sequenceNumString.getBytes("ASCII");
	    //System.out.println(sequenceNumByteArray.length);
	}catch(java.io.UnsupportedEncodingException e)
	    {
		e.printStackTrace();
	    }

	byte continuationCodeByte;
	if(continuationCode == true)
	    {
		//if it is continuation,it is represented as asterix '*'
		continuationCodeByte = (byte)42;
	    }
	else
	    {
		//if it continuationCode is false...it is represented as space ' '
		continuationCodeByte = (byte)32;
	    }
	try{
	    dos.write(sequenceNumByteArray);
	    dos.write((byte)typeCode);
	    dos.write(continuationCodeByte);
	}catch(Exception e) {
	    e.printStackTrace();	
	}
         
 }


    /**
     * Writes an ASCII version of the record header. This is not meant to be a definitive ascii representation,
     * merely to give something to print for debugging purposes. Ideally each field of the header should
     * be printed in the order is appears in the header in a visually appealing way.
     * 
     * @param out
     *            a Writer
     * 
     */
    public void writeASCII(PrintWriter out) throws IOException {
        writeASCII(out, "");
    }

    public void writeASCII(PrintWriter out, String indent) throws IOException {
        out.print(indent+"seq=" + getSequenceNum());
        out.print(" type=" + getTypeCode());
        out.println(" cont=" + isContinuation());
    }
    
    public ControlHeader(int sequenceNum, byte typeCode, 
			 boolean continuationCode) {
	this.sequenceNum = sequenceNum;
	this.typeCode = (byte)typeCode;
	this.continuationCode = continuationCode;
    }

    public ControlHeader(int sequenceNum, char typeCode, 
			 boolean continuationCode) {
	this(sequenceNum, (byte)typeCode, continuationCode);
    }

    public short getSize() { return 8;}
	
    public int getSequenceNum() {
	return sequenceNum;
    }
	
    public char getTypeCode() {
	return (char)typeCode;
    }
	
    public boolean isContinuation() {
	return continuationCode;
    }

    public String toString() {
	return getTypeCode()+"  "+getSequenceNum();
    }
    public static void tester(String fileName){

	DataOutputStream dos = null;
	try{
	    dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
	    ControlHeader controlHeaderObject = new ControlHeader(23,(byte)'D',true);
	    controlHeaderObject.write( dos);
	    dos.close();
	}catch(Exception e)
	    {
		e.printStackTrace();
	    }
    }
    public static void main (String[] args){
	ControlHeader.tester(args[0]);

    }
}

