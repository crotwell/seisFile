package edu.sc.seis.seisFile.mseed3.ehbag;

import edu.sc.seis.seisFile.TimeUtils;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Marker {
    String name;
    String type;
    ZonedDateTime time;
    String description;

    public Marker(String name, ZonedDateTime time) {
        this(name, time, "", "");
    }

    public Marker(String name, ZonedDateTime time, String type, String description) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.description = description;
    }

    public JSONObject asJSON() {
        JSONObject j = new JSONObject();
        j.put("n", name);
        j.put("tm", TimeUtils.toISOString(time.toInstant()));
        if (type.length() > 0) {j.put("mtype", type);}
        if (description.length() > 0) {j.put("desc", description);}
        return j;
    }

    public static Marker fromJSON(JSONObject j) {
        Marker m = null;
        if (j.has("tm") && j.has("n")) {
            ZonedDateTime time = TimeUtils.parseISOString(j.getString("tm")).atZone(ZoneId.of("UTC"));
            m = new Marker(j.getString("n"), time);
            if (j.has("desc")) {m.description = j.getString("desc");}
            if (j.has("mtype")) {m.type = j.getString("mtype");}
        }
        return m;
    }
}
