package edu.sc.seis.seisFile;

import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;

/**
 * Simple class to hold a lat/lon pair, with optional depth, defaults to 0.
 */
public class Location {

    double latitude;
    double longitude;

    double depth_meter = 0;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, double depth) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth_meter = depth;
    }

    public Location(Channel chan) {
        this.latitude = chan.getLatitudeFloat();
        this.longitude = chan.getLongitudeFloat();
        this.depth_meter = chan.getDepthFloat();
    }

    public Location(Event ev) {
        this(ev.getPreferredOrigin() != null ? ev.getPreferredOrigin() :
                (!ev.getOriginList().isEmpty() ? ev.getOriginList().get(0) : null));
    }

    public Location(Origin o) {
        this.latitude = o.getLatitude().getValue();
        this.longitude = o.getLongitude().getValue();
        this.depth_meter = o.getDepth().getValue();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDepthMeter() {
        return depth_meter;
    }
    public double getDepthKm() {
        return depth_meter /1000;
    }

    @Override
    public String toString() {
        return "(" + latitude +", " + longitude + ") at " + depth_meter+" m";
    }
}
