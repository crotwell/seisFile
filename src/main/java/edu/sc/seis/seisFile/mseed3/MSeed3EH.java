package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.Location;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.mseed3.ehbag.Marker;
import edu.sc.seis.seisFile.mseed3.ehbag.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;

public class MSeed3EH {

    /**
     * Creates a new empty extra header.
     */
    public MSeed3EH() {
        this(new JSONObject());
    }

    public MSeed3EH(JSONObject eh) {
        this.eh = eh;
        if (eh.has(BAG)) {
            bag = eh.getJSONObject(BAG);
        }
    }

    public JSONObject getEH() {
        return eh;
    }

    public JSONObject getBagEH() {
        if (bag == null) {
            bag = new JSONObject();
            eh.put(BAG, bag);
        }
        return bag;
    }

    public void insertLocation(JSONObject json, Location loc) {
        json.put(LATITUDE, loc.getLatitude());
        json.put(LONGITUDE, loc.getLongitude());
        json.put(DEPTH, loc.getDepthMeter());
    }

    public void addToBag(Channel chan) {
        if (chan == null && eh.has(BAG)) {
            getBagEH().remove(CHANNEL);
            return;
        }
        JSONObject bagEh = getBagEH();
        JSONObject ch = new JSONObject();
        if (chan.getLatitude() != null && chan.getLongitude() != null && chan.getDepth() != null) {
            insertLocation(ch, new Location(chan));
        }
        if (chan.getElevation() != null) {
            ch.put(ELEVATION, chan.getElevation().getValue());
        } else if (chan.getStation().getElevation() != null) {
            ch.put(ELEVATION, chan.getStation().getElevation().getValue());
        }
        if (chan.getAzimuth() != null) {
            ch.put(AZ, chan.getAzimuth());
        }
        if (chan.getDip() != null) {
            ch.put(DIP, chan.getDip());
        }
        bagEh.put(CHANNEL, ch);
    }

    public void addToBag(Path path) {
        if (path == null && eh.has(BAG)) {
            getBagEH().remove(PATH);
            return;
        }
        JSONObject bagEh = getBagEH();
        bagEh.put(PATH, path.asJSON());
    }

    public void addToBag(Marker marker) {
        if (marker == null) {
            return;
        }
        JSONObject bagEh = getBagEH();
        if ( ! bagEh.has(MARKERS)) {
            bagEh.put(MARKERS, new JSONArray());
        }
        JSONArray markers = bagEh.getJSONArray(MARKERS);
        markers.put(marker.asJSON());
    }

    public void setTimeseriesUnit(String siUnit) {
        JSONObject bagEh = getBagEH();
        if (! bagEh.has(Y)) {
            bagEh.put(Y, new JSONObject());
        }
        JSONObject y = bagEh.getJSONObject(Y);
        y.put(SI, siUnit);
    }

    public String getTimeseriesUnit() {
        JSONObject bagEh = getBagEH();
        if (bagEh.has(Y)) {
            JSONObject y = bagEh.getJSONObject(Y);
            return y.optString(SI);
        }
        return null;
    }

    public Location channelLocation() {
        Location loc = null;
        if (bag != null) {
            if (bag.has(CHANNEL)) {
                JSONObject ch = bag.getJSONObject(CHANNEL);
                float depth = 0;
                if (ch.has(DEPTH)) {
                    depth = ch.getFloat(DEPTH);
                }
                loc = new Location(ch.getFloat(LATITUDE), ch.getFloat(LONGITUDE), depth);
            }
        }
        return loc;
    }

    public void addToBag(Event q) {
        if (q == null) { return; }
        JSONObject bagEh = getBagEH();
        JSONObject ev = new JSONObject();
        ev.put(ID, q.getPublicId());
        JSONObject o = new JSONObject();
        Origin origin = null;
        if (q.getPreferredOrigin() != null) {
            origin = q.getPreferredOrigin();
        } else if (!q.getOriginList().isEmpty()) {
            origin = q.getOriginList().get(0);
        }
        if (origin != null) {
            insertLocation(o, new Location(origin));
            o.put(TIME, TimeUtils.toISOString(origin.getTime().asInstant()));
            ev.put(ORIGIN, o);
        }
        if (q.getPreferredMagnitude() != null) {
            Magnitude m = q.getPreferredMagnitude();
            JSONObject mag = new JSONObject();
            mag.put(MAGVALUE, m.getMag().getValue());
            mag.put(MAGTYPE, m.getType());
            ev.put(MAGNITUDE, mag);
        }
        bagEh.put(EVENT, ev);
    }

    public void addOriginToBag(float lat, float lon, float depthMeter, Instant originTime) {
        JSONObject bagEh = getBagEH();
        JSONObject ev = bagEh.has(EVENT) ? bagEh.getJSONObject(EVENT) : new JSONObject();
        bagEh.put(EVENT, ev);
        JSONObject o = ev.has(ORIGIN) ? ev.getJSONObject(ORIGIN) : new JSONObject();
        ev.put(ORIGIN, o);
        o.put(LATITUDE, lat);
        o.put(LONGITUDE, lon);
        o.put(DEPTH, depthMeter);
        o.put(TIME, TimeUtils.toISOString(originTime));
    }

    public void addMagnitudeToBag(float magVal, String magType) {
        JSONObject bagEh = getBagEH();
        JSONObject ev = bagEh.has(EVENT) ? bagEh.getJSONObject(EVENT) : new JSONObject();
        bagEh.put(EVENT, ev);
        JSONObject mag = new JSONObject();
        mag.put(MAGVALUE, magVal);
        mag.put(MAGTYPE, magType);
        ev.put(MAGNITUDE, mag);
    }

    public Location quakeLocation() {
        Location loc = null;
        if (bag != null) {
            if (bag.has(EVENT)) {
                JSONObject ev = bag.getJSONObject(EVENT);
                if (ev.has(ORIGIN)) {
                    JSONObject or = ev.getJSONObject(ORIGIN);
                    float depth = 0;
                    if (or.has(DEPTH)) {
                        depth = or.getFloat(DEPTH);
                    }
                    loc = new Location(or.getFloat(LATITUDE), or.getFloat(LONGITUDE), depth);
                }
            }
        }
        return loc;
    }
    public Instant quakeTime() {
        Instant time = null;
        if (bag != null) {
            if (bag.has(EVENT)) {
                JSONObject ev = bag.getJSONObject(EVENT);
                if (ev.has(ORIGIN)) {
                    JSONObject or = ev.getJSONObject(ORIGIN);
                    return TimeUtils.parseISOString(or.getString(TIME));
                }
            }
        }
        return time;
    }

    public void setQuakeTime(Instant otime) {
        JSONObject bag = getBagEH();
        if ( ! bag.has(EVENT)) {
            bag.put(EVENT, new JSONObject());
        }
        JSONObject ev = bag.getJSONObject(EVENT);
        if ( ! ev.has(ORIGIN)) {
            ev.put(ORIGIN, new JSONObject());
        }
        JSONObject or = ev.getJSONObject(ORIGIN);
        or.put(TIME, TimeUtils.toISOString(otime));
    }

    public Float gcarc() {
        if (bag != null) {
            if (bag.has(PATH)) {
                JSONObject path = bag.getJSONObject(PATH);
                if (path.has(GCARC)) {
                    return path.getFloat(GCARC);
                }
            }
        }
        return null;
    }

    public void putGcarc(float gcarc) {
        JSONObject bag = getBagEH();
        if (! bag.has(PATH)) {
            bag.put(PATH, new JSONObject());
        }
        bag.getJSONObject(PATH).put(GCARC, gcarc);
    }

    JSONObject eh;
    JSONObject bag = null;

    public static final String BAG = "bag";
    public static final String CHANNEL = "ch";
    public static final String EVENT = "ev";
    public static final String ID = "id";
    public static final String ORIGIN = "or";
    public static final String TIME = "tm";
    public static final String DEPTH = "dp";
    public static final String ELEVATION = "el";
    public static final String LATITUDE = "la";
    public static final String LONGITUDE = "lo";
    public static final String MAGNITUDE = "mag";
    public static final String MAGVALUE = "v";
    public static final String MAGTYPE = "t";
    public static final String MARKERS = "mark";
    public static final String PATH = "path";
    public static final String GCARC = "gcarc";
    public static final String AZ = "az";
    public static final String BAZ = "baz";
    public static final String DIP = "dip";
    public static final String Y = "y";
    public static final String SI = "si";
    public static final String PROC = "proc";
}
