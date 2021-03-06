package edu.sc.seis.seisFile;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static final String DASH = "-";
    public static final ZoneId TZ_UTC = ZoneId.of("UTC");
    public static final String ZULU = "Z";
    public static DateTimeFormatter getCompactDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(TZ_UTC);
    }
    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ISO_INSTANT;
    }
    public static Instant parseISOString(String time) {
        if (time.charAt(8) == 'T') {
            // need dashes, yyyymmdd to yyyy-mm-dd
            time = time.substring(0, 4)+DASH+time.substring(4, 6)+DASH+time.substring(6);
        }
        if ( ! time.endsWith(ZULU)) {
            time = time+ZULU;
        }
        return Instant.parse(time);
    }
    public static String toISOString(Instant time) {
        return getDateTimeFormatter().format(time);
    }
    public static String toCompactISOString(Instant time) {
        return getCompactDateTimeFormatter().format(time);
    }
    public static final Duration ONE_MICROSECOND = Duration.ofNanos(1000);
    public static final Duration ONE_MILLISECOND = Duration.ofMillis(1);
    public static final Duration ONE_SECOND = Duration.ofSeconds(1);
    public static final Duration ONE_MINUTE = Duration.ofMinutes(1);
    public static final Duration ONE_HOUR = Duration.ofHours(1);
    public static final Duration ONE_DAY = Duration.ofDays(1);
    public static final Duration ONE_WEEK = Duration.ofDays(7);
    public static final Duration ONE_FORTNIGHT = Duration.ofDays(14);
    public static final Duration ONE_MONTH = Duration.ofDays(30);
    public static final Duration ZERO_DURATION = Duration.ofNanos(0);
    public static final int NANOS_IN_SEC_INT = 1000000000;
    public static final double NANOS_IN_SEC = NANOS_IN_SEC_INT;
    public static final int NANOS_IN_MILLI = 1000000;
    public static final int NANOS_IN_TENTH_MILLI = 100000;
    public static final Duration TENTH_MILLI = Duration.ofNanos(NANOS_IN_TENTH_MILLI);
    public static final int SECS_IN_DAY = 86400;
    
    public static final Instant wayPast = parseISOString("1099-01-01T00:00:00.000000Z");
    public static final Instant future =parseISOString("2499-01-01T00:00:00.000000Z");
    /** future plus one day so that is is after(future)
     */
    public static final Instant futurePlusOne = parseISOString("2499-01-02T00:00:00.000000Z");
    public static Duration durationFromSeconds(double seconds) {
        return Duration.ofNanos(Math.round(NANOS_IN_SEC*seconds));
    }
    
    public static double durationToDoubleSeconds(Duration d) {
        return d.toNanos()/NANOS_IN_SEC;
    }
    
    public static float durationToFloatDays(Duration d) {
        return (float)(d.toNanos()/NANOS_IN_SEC/SECS_IN_DAY);
    }
    
    public static Instant instantFromEpochSeconds(double epochSec) {
        double epochEvenSeconds = Math.floor(epochSec);
        return Instant.ofEpochSecond(Math.round(epochEvenSeconds), Math.round(NANOS_IN_SEC*(epochSec-epochEvenSeconds)));
    }
    
    public static double instantToEpochSeconds(Instant instant) {
        return instant.getEpochSecond() + instant.getNano() / NANOS_IN_SEC;
    }

    public static Duration multiply(Duration dur, double value) {
        return Duration.ofNanos(Math.round(dur.toNanos() * value));
    }

    public static Duration divide(Duration dur, double value) {
        return Duration.ofNanos(Math.round(dur.toNanos() / value));
    }
    
    public static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(TZ_UTC);
    }
}
