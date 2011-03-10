package edu.sc.seis.seisFile.mseed;

/** Convience class to hold begin and end Btime, along with simple methods to test if another
 * Btime in inside the range.
 * @author crotwell
 * 
 * Created on Mar 10, 2011
 */
public class BtimeRange {
    
    Btime begin;
    Btime end;
    
    public BtimeRange(Btime begin, Btime end) {
        super();
        this.begin = begin;
        this.end = end;
    }

    public Btime getBegin() {
        return begin;
    }
    
    public Btime getEnd() {
        return end;
    }
    
    public boolean overlaps(Btime btime) {
        return btime.afterOrEquals(begin) && end.afterOrEquals(btime);
    }
    
    public boolean overlaps(BtimeRange range) {
        return ! (range.getBegin().after(getEnd()) || getBegin().after(range.getEnd()));
    }
}
