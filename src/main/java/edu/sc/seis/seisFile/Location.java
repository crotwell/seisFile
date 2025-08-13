package edu.sc.seis.seisFile;

import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;

import java.util.Locale;
import java.util.Objects;

/**
 * Simple class to hold a lat/lon pair, with optional depth, defaults to 0.
 */
public class Location implements LatLonLocatable {

    double latitude;
    double longitude;

    Double depth_meter = null;

    String description = null;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, double depth_meter) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth_meter = depth_meter;
    }

    public Location(Station sta) {
        this.latitude = sta.getLatitudeFloat();
        this.longitude = sta.getLongitudeFloat();
        this.depth_meter = null;
        setDescription(sta.getSourceId());
    }

    public Location(Channel chan) {
        this.latitude = chan.getLatitudeFloat();
        this.longitude = chan.getLongitudeFloat();
        if (chan.getDepth() != null) {
            this.depth_meter = Double.valueOf(chan.getDepthFloat());
        }
        setDescription(chan.getSourceId());
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

    /**
     *
     * @return true if this Location has an optional description.
     */
    public boolean hasDescription() {
        return description != null && ! description.isEmpty();
    }

    /**
     *
     * @return an optional description, like event time or station code.
     */
    public String getDescription() {
        return description;
    }
    public void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public String toString() {
        String out = "(" + latitude +", " + longitude + ")";
        out += hasDepth() ? " at " + depth_meter+" m" : "";
        if (hasDescription()) {
            out += " "+getDescription();
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Double.compare(latitude, location.latitude) == 0 &&
                Double.compare(longitude, location.longitude) == 0 &&
                Objects.equals(depth_meter, location.depth_meter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, depth_meter);
    }

    public static String formatLatLon(double latlon) {
        return String.format(locale, latLonFormat, latlon);
    }

    public static Locale locale = Locale.ROOT;
    public static String latLonFormat = "%8.2f";

    @Override
    public Location asLocation() {
        return this;
    }

    @Override
    public String getLocationDescription() {
        if (hasDescription()) {
            return getDescription();
        } else {
            return createLocationDescription(this);
        }
    }

    public static String createLocationDescription(Location loc) {
        String out = Location.formatLatLon(loc.latitude).trim()+"/"+Location.formatLatLon(loc.longitude).trim();
        if (loc.hasDepth()) {
            out += " "+Location.formatLatLon(loc.depth_meter).trim()+" m";
        }
        return out;
    }
}
