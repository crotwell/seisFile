package edu.sc.seis.seisFile.gcf;

import java.io.DataOutput;
import java.io.IOException;


public class GCFStatusBlock extends AbstractGCFBlock {

    public GCFStatusBlock(GCFHeader header, String status) {
        super(header);
        this.header = header;
        this.status = status;
    }
    
    public int getSize() {
        int size = 24;
        size += ((status.length()+3) / 4) * 4; // round up to multiple of 4
        return  size;
    }
    
    public void write(DataOutput out) throws NumberFormatException, IOException {
        header.write(out);
        out.writeChars(status);
        for (int i = 0; i < 4 - status.length() % 4; i++) {
            out.writeChar(' ');
        }
    }
    
    String status;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        GCFStatusBlock other = (GCFStatusBlock)obj;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }
    
    
}
