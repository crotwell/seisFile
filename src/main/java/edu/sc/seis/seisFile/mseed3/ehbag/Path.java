package edu.sc.seis.seisFile.mseed3.ehbag;

import org.json.JSONObject;

import static edu.sc.seis.seisFile.mseed3.MSeed3EHKeys.*;

public class Path {
    public Path() {
        this(null, null, null);
    }
    public Path(Float gcarc, Float az, Float baz) {
        this.gcarc = (gcarc != null && Float.isFinite(gcarc)) ? gcarc : null;
        this.az = (az != null && Float.isFinite(az)) ? az : null;
        this.baz = (baz != null && Float.isFinite(baz)) ? baz : null;
    }

    public JSONObject asJSON() {
        JSONObject j = new JSONObject();
        if (gcarc != null) {
            j.put(GCARC, gcarc.floatValue());
        }
        if (az != null) {
            j.put(AZ, az.floatValue());
        }
        if (baz != null) {
            j.put(BAZ, baz.floatValue());
        }
        return j;
    }

    public static Path fromJSON(JSONObject j) {
        Path path = new Path();
        if (j.has(GCARC)) {
            path.gcarc = j.getFloat(GCARC);
        }
        if (j.has(AZ)) {
            path.az = j.getFloat(AZ);
        }
        if (j.has(BAZ)) {
            path.baz = j.getFloat(BAZ);
        }
        return path;
    }

    public String toString() {
        return "gcarc: "+gcarc+" az: "+az+" baz: "+baz;
    }

    public boolean notAllNull() {
        return gcarc != null || az != null || baz != null;
    }

    Float gcarc;
    Float az;
    Float baz;
}
