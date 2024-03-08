package edu.sc.seis.seisFile.mseed3;

import edu.sc.seis.seisFile.Location;
import edu.sc.seis.seisFile.TimeUtils;
import org.json.JSONObject;

import java.time.Instant;

public class MSeed3EH {

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

    public Location channelLocation() {
        Location loc = null;
        if (bag != null) {
            if (bag.has("ch")) {
                JSONObject ch = bag.getJSONObject("ch");
                float depth = 0;
                if (ch.has("dp")) {
                    depth = ch.getFloat("dp");
                }
                loc = new Location(ch.getFloat("la"), ch.getFloat("lo"), depth);
            }
        }
        return loc;
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

    public Float gcarc() {
        if (bag != null) {
            if (bag.has("path")) {
                JSONObject path = bag.getJSONObject("path");
                if (path.has("gcarc")) {
                    return path.getFloat("gcarc");
                }
            }
        }
        return null;
    }

    JSONObject eh;
    JSONObject bag = null;
}
