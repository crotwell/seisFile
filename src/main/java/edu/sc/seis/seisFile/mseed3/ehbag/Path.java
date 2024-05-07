package edu.sc.seis.seisFile.mseed3.ehbag;

import edu.sc.seis.seisFile.mseed3.MSeed3EH;
import org.json.JSONObject;

public class Path {
    public Path() {
        this(null, null, null);
    }
    public Path(Float gcarc, Float az, Float baz) {
        this.gcarc = Float.isFinite(gcarc) ? gcarc : null;
        this.az = Float.isFinite(az) ? az : null;
        this.baz = Float.isFinite(baz) ? baz : null;
    }

    public JSONObject asJSON() {
        JSONObject j = new JSONObject();
        if (gcarc != null) {
            j.put(MSeed3EH.GCARC, gcarc.floatValue());
        }
        if (az != null) {
            j.put(MSeed3EH.AZ, az.floatValue());
        }
        if (baz != null) {
            j.put(MSeed3EH.BAZ, baz.floatValue());
        }
        return j;
    }

    public static Path fromJSON(JSONObject j) {
        Path path = new Path();
        if (j.has(MSeed3EH.GCARC)) {
            path.gcarc = j.getFloat(MSeed3EH.GCARC);
        }
        if (j.has(MSeed3EH.AZ)) {
            path.az = j.getFloat(MSeed3EH.AZ);
        }
        if (j.has(MSeed3EH.BAZ)) {
            path.baz = j.getFloat(MSeed3EH.BAZ);
        }
        return path;
    }

    public String toString() {
        return "gcarc: "+gcarc+" az: "+az+" baz: "+baz;
    }

    Float gcarc;
    Float az;
    Float baz;
}
