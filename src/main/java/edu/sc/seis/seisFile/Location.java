package edu.sc.seis.seisFile;

import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;

import java.util.Objects;

/**
 * Simple class to hold a lat/lon pair, with optional depth, defaults to 0.
 */
public class Location {

    double latitude;
    double longitude;

    Double depth_meter = null;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, double depth) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth_meter = depth;
    }

    public Location(Station sta) {
        this.latitude = sta.getLatitudeFloat();
        this.longitude = sta.getLongitudeFloat();
        this.depth_meter = null;
    }

    public Location(Channel chan) {
        this.latitude = chan.getLatitudeFloat();
        this.longitude = chan.getLongitudeFloat();
        if (chan.getDepth() != null) {
            this.depth_meter = Double.valueOf(chan.getDepthFloat());
        }
    }

    public Location(Event ev) {
        this(ev.getPreferredOrigin() != null ? ev.getPreferredOrigin() :
                (!ev.getOriginList().isEmpty() ? ev.getOriginList().get(0) : null));
    }

    public Location(Origin o) {
        this.latitude = o.getLatitude().getValue();
        this.longitude = o.getLongitude().getValue();
        if (o.getDepth() != null) {
            this.depth_meter = Double.valueOf(o.getDepth().getValue());
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     *
     * @return true if this Location has a depth set.
     */
    public boolean hasDepth() {
        return getDepthMeter() != null;
    }

    /**
     *
     * @return depth in meters if set, otherwise null
     */
    public Double getDepthMeter() {
        return depth_meter;
    }
    /**
     *
     * @return depth in kilometers if set, otherwise null
     */
    public Double getDepthKm() {

        return hasDepth() ? getDepthMeter() /1000 : null;
    }

    @Override
    public String toString() {
        String out = "(" + latitude +", " + longitude + ")";
        out += hasDepth() ? " at " + depth_meter+" m" : "";
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Double.compare(latitude, location.latitude) == 0 && Double.compare(longitude, location.longitude) == 0 && Objects.equals(depth_meter, location.depth_meter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, depth_meter);
    }
}
