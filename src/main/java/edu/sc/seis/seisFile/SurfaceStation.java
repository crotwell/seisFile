package edu.sc.seis.seisFile;

import edu.sc.seis.seisFile.fdsnws.stationxml.Station;

import java.util.Objects;

/**
 * Locatable to represent a station, but with depth forced to be at the surface to avoid
 * depth being undefined.
 */
public class SurfaceStation implements LatLonLocatable {

    public SurfaceStation(Station station) {
        this.station = station;
    }

    @Override
    public Location asLocation() {
        Location loc = new Location(station);
        loc.depth_meter = 0.0;
        return loc;
    }

    @Override
    public String getLocationDescription() {
        return station.getLocationDescription();
    }

    Station station;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SurfaceStation)) return false;
        SurfaceStation that = (SurfaceStation) o;
        return Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(station);
    }

    @Override
    public String toString() {
        return station.toString()+" at surface";
    }
}
