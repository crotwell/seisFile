
package edu.sc.seis.seisFile.mseed;

/**
 * BlocketteUnknown.java
 *
 *
 * Created: Mon Apr  5 15:48:51 1999
 *
 * @author Philip Crotwell
 * @version
 */

import java.io.IOException;
import java.io.Writer;

public class BlocketteUnknown extends Blockette {
    

    public BlocketteUnknown(byte[] info, int type) {
    this.info = info;
    this.type = type;
    }
       
    public int getType() {
    return type;
    }
    
    public String getName() {
    return "Unknown";
    }

    public int getSize() {
    return info.length;
    }

    public byte[] toBytes() {
    return info;
    }
    
    public void writeASCII(Writer out) throws IOException {
        out.write("Blockette UNKNOWN");
    }
  
    protected int type;

    protected byte[] info;

} // BlocketteUnknown
