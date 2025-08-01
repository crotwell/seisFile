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
        String out = Location.formatLatLon(loc.latitude).trim()+"/"+Location.formatLatLon(loc.longitude).trim();
        if (loc.hasDepth()) {
            out += " "+Location.formatLatLon(loc.depth_meter).trim()+" m";
        }
        return out;
    }

    Location loc;
}
