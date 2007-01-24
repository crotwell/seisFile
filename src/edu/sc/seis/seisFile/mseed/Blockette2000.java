package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Blockette2000 extends DataBlockette {

    public Blockette2000(String[] headerFields, byte[] opaqueData) {
        super(opaqueData.length + FIXED_HEADER_LENGTH
                + calcHeaderFieldLength(headerFields));
        System.arraycopy(Utility.intToByteArray(info.length),
                         2,
                         info,
                         BLOCKETTE_LENGTH,
                         2);
        info[NUM_HEADER_FIELD] = (byte)headerFields.length;
        int pos = HEADER_FIELD;
        for(int i = 0; i < headerFields.length; i++) {
            byte[] headerBytes;
            try {
                headerBytes = (headerFields[i] + '~').getBytes("US-ASCII");
            } catch(UnsupportedEncodingException e) {
                throw new RuntimeException("Java was unable to find the US-ASCII character encoding.");
            }
            System.arraycopy(headerBytes, 0, info, pos, headerBytes.length);
            pos += headerBytes.length;
        }
        info[OPAQUE_OFFSET] = (byte)pos;
        System.arraycopy(opaqueData, 0, info, pos, opaqueData.length);
    }

    private static int calcHeaderFieldLength(String[] headerFields) {
        int len = headerFields.length;// A byte for each terminating '~'
        for(int i = 0; i < headerFields.length; i++) {
            len += headerFields[i].length();
        }
        return len;
    }

    public Blockette2000(byte[] info) {
        super(info);
    }

    public String getName() {
        return "Variable Length Opaque Data Blockette";
    }

    public int getSize() {
        return info.length;
    }

    public int getType() {
        return 2000;
    }

    public String getHeaderField(int i) {
        int curHeader = 0;
        int start = HEADER_FIELD;
        for(; start < info.length && curHeader != i; start++) {
            if(info[start] == '~') {
                curHeader++;
            }
        }
        int end = start;
        while(info[end] != '~') {
            end++;
        }
        return new String(info, start, end - start);
    }

    public int getNumHeaders() {
        return info[NUM_HEADER_FIELD];
    }

    public byte[] getOpaqueData() {
        byte[] opaque = new byte[info.length - info[OPAQUE_OFFSET]];
        System.arraycopy(info, info[OPAQUE_OFFSET], opaque, 0, opaque.length);
        return opaque;
    }

    public void writeASCII(Writer out) throws IOException {
        out.write("Blockette2000");
    }
    
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(o instanceof Blockette2000){
            byte[] oinfo = ((Blockette2000)o).info;
            if(info.length != oinfo.length){
                return false;
            }
            for(int i = 0; i < oinfo.length; i++) {
                if(info[i] != oinfo[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static final int BLOCKETTE_LENGTH = 4;

    private static final int OPAQUE_OFFSET = 6;

    private static final int NUM_HEADER_FIELD = 14;

    private static final int HEADER_FIELD = 15;

    private static final int FIXED_HEADER_LENGTH = 15;
}
