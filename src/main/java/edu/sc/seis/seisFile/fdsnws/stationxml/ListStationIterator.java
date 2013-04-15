package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.Iterator;
import java.util.List;

import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationIterator;

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
