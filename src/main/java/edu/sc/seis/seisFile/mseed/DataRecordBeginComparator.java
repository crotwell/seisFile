package edu.sc.seis.seisFile.mseed;

import java.util.Comparator;


public class DataRecordBeginComparator implements Comparator<DataRecord> {

    public int compare(DataRecord o1, DataRecord o2) {
        Btime b1 = o1.getHeader().getStartBtime();
        Btime b2 = o2.getHeader().getStartBtime();
        return btimeComparator.compare(b1, b2);
    }
    
    BtimeComparator btimeComparator = new BtimeComparator();
}

