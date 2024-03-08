package edu.sc.seis.seisFile;

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
}
