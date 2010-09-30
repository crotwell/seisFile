package edu.sc.seis.seisFile.mseed;

/**
 * SeedRecord.java
 * 
 * 
 * Created: Thu Apr 8 11:54:07 1999
 * 
 * @author Philip Crotwell
 * @version
 */
import java.util.ArrayList;
import java.util.List;

public abstract class SeedRecord {

    public SeedRecord(ControlHeader header) {
        this.header = header;
    }

    public void addBlockette(Blockette b) throws SeedFormatException {
        blockettes.add(b);
    }

    public Blockette[] getBlockettes() {
        return blockettes.toArray(new Blockette[0]);
    }

    public Blockette getUniqueBlockette(int type) throws SeedFormatException {
        Blockette[] b = getBlockettes(type);
        if (b.length == 1) {
            return b[0];
        } else if (b.length == 0) {
            if (type == 1000) {
                // special case as b1000 is required in mseed
                throw new MissingBlockette1000();
            }
            throw new SeedFormatException("No blockettes of type "+type);
        } else {
            throw new SeedFormatException("Multiple blockettes of type "+type);
        }
    }

    public int getNumBlockettes(int type) throws SeedFormatException {
        int out = 0;
        for (Blockette b : blockettes) {
            if(b.getType() == type) {
                out++;
            }
        }
        return out;
    }

    public Blockette[] getBlockettes(int type) {
        List<Blockette> out = new ArrayList<Blockette>();
        for (Blockette b : blockettes) {
            if(b.getType() == type) {
                out.add(b);
            }
        }
        return out.toArray(new Blockette[0]);
    }

    public String toString() {
        String s = "Record for " + header + "\n";
        s += "Blockettes:\n";
        for(int i = 0; i < blockettes.size(); i++) {
            s += blockettes.get(i) + "\n";
        }
        return s;
    }

    protected ControlHeader header;

    protected List<Blockette> blockettes = new ArrayList<Blockette>();
} // SeedRecord
