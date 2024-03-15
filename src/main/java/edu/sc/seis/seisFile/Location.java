package edu.sc.seis.seisFile;

import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;

/**
 * Simple class to hold a lat/lon pair, with optional depth.
 */
public class Location {

    float latitude = 0;
    float longitude = 0;

    float depth_meter = 0;

    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(float latitude, float longitude, float depth) {
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
        Origin o = ev.getPreferredOrigin();
        this.latitude = o.getLatitude().getValue();
        this.longitude = o.getLongitude().getValue();
        this.depth_meter = o.getDepth().getValue();
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getDepthMeter() {
        return depth_meter;
    }
    public float getDepthKm() {
        return depth_meter /1000;
    }

    @Override
    public String toString() {
        return "(" + latitude +", " + longitude + ") at " + depth_meter+" m";
    }
}
