package edu.sc.seis.seisFile;

public class LatLonSimple implements LatLonLocatable {
    public LatLonSimple(Location loc) {
        this.loc = loc;
    }
    public LatLonSimple(double lat, double lon) {
        this(new Location(lat, lon));
        this.loc.setDescription(getLocationDescription());
    }
    public LatLonSimple(double lat, double lon, double depth_meter) {
        this(new Location(lat, lon, depth_meter));
        this.loc.setDescription(getLocationDescription());
    }

    @Override
    public Location asLocation() {
        return loc;
    }

    @Override
    public String getLocationDescription() {
        return Location.createLocationDescription(loc);
    }

    Location loc;
}
