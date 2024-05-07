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
        if (eh.has("bag")) {
            bag = eh.getJSONObject("bag");
        }
    }

    public JSONObject getEH() {
        return eh;
    }

    public JSONObject getBagEH() {
        if (bag == null) {
            bag = new JSONObject();
            eh.put("bag", bag);
        }
        return bag;
    }

    public void insertLocation(JSONObject json, Location loc) {
        json.put("la", loc.getLatitude());
        json.put("lo", loc.getLongitude());
        json.put("dp", loc.getDepthMeter());
    }

    public void addToBag(Channel chan) {
        if (chan == null && eh.has(BAG)) {
            getBagEH().remove(CHANNEL);
            return;
        }
        JSONObject bagEh = getBagEH();
        JSONObject ch = new JSONObject();
        insertLocation(ch, new Location(chan));
        ch.put("el", chan.getElevation().getValue());
        ch.put("az", chan.getAzimuth());
        ch.put("dip", chan.getDip());
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

    public Location channelLocation() {
        Location loc = null;
        if (bag != null) {
            if (bag.has(CHANNEL)) {
                JSONObject ch = bag.getJSONObject(CHANNEL);
                float depth = 0;
                if (ch.has("dp")) {
                    depth = ch.getFloat("dp");
                }
                loc = new Location(ch.getFloat("la"), ch.getFloat("lo"), depth);
            }
        }
        return loc;
    }

    public void addToBag(Event q) {
        if (q == null) { return; }
        JSONObject bagEh = getBagEH();
        JSONObject ev = new JSONObject();
        ev.put("id", q.getPublicId());
        JSONObject o = new JSONObject();
        Origin origin = null;
        if (q.getPreferredOrigin() != null) {
            origin = q.getPreferredOrigin();
        } else if (q.getOriginList().size() > 0) {
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
            mag.put("v", m.getMag().getValue());
            mag.put("t", m.getType());
            ev.put("mag", mag);
        }
        bagEh.put(EVENT, ev);
    }

    public Location quakeLocation() {
        Location loc = null;
        if (bag != null) {
            if (bag.has("ev")) {
                JSONObject ev = bag.getJSONObject("ev");
                if (ev.has("or")) {
                    JSONObject or = ev.getJSONObject("or");
                    float depth = 0;
                    if (or.has("dp")) {
                        depth = or.getFloat("dp");
                    }
                    loc = new Location(or.getFloat("la"), or.getFloat("lo"), depth);
                }
            }
        }
        return loc;
    }
    public Instant quakeTime() {
        Instant time = null;
        if (bag != null) {
            if (bag.has("ev")) {
                JSONObject ev = bag.getJSONObject("ev");
                if (ev.has("or")) {
                    JSONObject or = ev.getJSONObject("or");
                    return TimeUtils.parseISOString(or.getString("tm"));
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
                if (path.has("gcarc")) {
                    return path.getFloat("gcarc");
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
    public static final String ORIGIN = "or";
    public static final String TIME = "tm";
    public static final String MARKERS = "mark";
    public static final String PATH = "path";
    public static final String GCARC = "gcarc";
    public static final String AZ = "az";
    public static final String BAZ = "baz";
}
