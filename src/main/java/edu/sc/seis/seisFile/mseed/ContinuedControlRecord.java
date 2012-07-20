package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class ContinuedControlRecord extends ControlRecord {

    public ContinuedControlRecord(ControlRecord first) {
        super(first.getControlHeader());
        subRecords.add(first);
    }

    public void addContinuation(List<ControlRecord>  nextRecordList) {
        for (ControlRecord cr : nextRecordList) {
            addContinuation(cr);
        }
    }
    
    public void addContinuation(ControlRecord nextRecord) {
        subRecords.add(nextRecord);
    }
    
    @Override
    public void addBlockette(Blockette b) throws SeedFormatException {
        subRecords.get(subRecords.size()-1).addBlockette(b);
    }

    @Override
    public Blockette[] getBlockettes() {
        PartialBlockette prior = null;
        List<Blockette> out = new ArrayList<Blockette>();
        for (ControlRecord cr : subRecords) {
            Blockette[] subB = cr.getBlockettes();
            for (Blockette b : subB) {
                if (b instanceof PartialBlockette) {
                    if (prior == null) {
                        prior = (PartialBlockette)b;
                    } else {
                        prior = PartialBlockette.combine(prior, (PartialBlockette)b);
                        if (prior.getBytesRead() == prior.getTotalSize()) {
                            // must have finished last section of partial blockette
                            // turn into real and add
                            try {
                                out.add(SeedRecord.getBlocketteFactory().parseBlockette(prior.getType(),
                                                                                    prior.toBytes(),
                                                                                    true));
                                prior = null;
                            } catch(Exception e) {
                                throw new RuntimeException("Unable to combine partial blockettes into a single real bloackette.", e);
                            }
                        }
                    }
                } else {
                    if (prior != null) {
                        throw new RuntimeException("Found regular blockette waiting for rest of partial blockette: "+prior.getBytesRead()+" out of "+prior.getTotalSize()+" bytes.");
                    }
                    out.add(b);
                }
            }
        }
        if (prior != null) {
            throw new RuntimeException("Found partial blockette at end, rest of bytes missing: "+prior.getBytesRead()+" out of "+prior.getTotalSize()+" bytes.");
        }
        return out.toArray(new Blockette[out.size()]);
    }


    @Override
    public int getNumBlockettes(int type) throws SeedFormatException {
        return getBlockettes(type).length;
    }

    @Override
    public Blockette[] getBlockettes(int type) {
        List<Blockette> out = new ArrayList<Blockette>();
        for (ControlRecord cr : subRecords) {
            Blockette[] subB = cr.getBlockettes();
            for (Blockette b : subB) {
                if (b.getType() == type) {
                    out.add(b);
                }
            }
        }
        return out.toArray(new Blockette[out.size()]);
    }

    @Override
    public void writeASCII(PrintWriter out, String indent) throws IOException {
        out.print(indent+"ContinuedControlRecord");
        getControlHeader().writeASCII(out, indent+"  ");
        for (ControlRecord cr : subRecords) {
            cr.writeASCII(out, indent+"    ");
        }
    }

    public List<ControlRecord> getSubRecords() {
        return subRecords;
    }

    List<ControlRecord> subRecords = new ArrayList<ControlRecord>();
}
