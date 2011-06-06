package edu.sc.seis.seisFile.stationxml;

import java.util.Iterator;
import java.util.List;

public class ListStationIterator extends StationIterator {

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Station next() {
        return it.next();
    }

    public ListStationIterator(Iterator<Station> it) {
        this.it = it;
    }
    public ListStationIterator(List<Station> list) {
        this.it = list.iterator();
    }

    Iterator<Station> it;
}
