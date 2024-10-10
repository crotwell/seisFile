package edu.sc.seis.seisFile.mseed3.ehbag;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.mseed3.MSeed3EHKeys;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static edu.sc.seis.seisFile.mseed3.MSeed3EHKeys.*;

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
        j.put(MARKER_NAME, name);
        j.put(TIME, TimeUtils.toISOString(time.toInstant()));
        if (type.length() > 0) {j.put(MARKER_TYPE, type);}
        if (description.length() > 0) {j.put(MARKER_DESC, description);}
        return j;
    }

    public static Marker fromJSON(JSONObject j) {
        Marker m = null;
        if (j.has(TIME) && j.has(MARKER_NAME)) {
            ZonedDateTime time = TimeUtils.parseISOString(j.getString(TIME)).atZone(ZoneId.of("UTC"));
            m = new Marker(j.getString(MARKER_NAME), time);
            if (j.has(MARKER_DESC)) {m.description = j.getString(MARKER_DESC);}
            if (j.has(MARKER_TYPE)) {m.type = j.getString(MARKER_TYPE);}
        }
        return m;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}
