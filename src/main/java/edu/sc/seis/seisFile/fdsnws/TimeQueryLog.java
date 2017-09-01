package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.sc.seis.seisFile.TimeUtils;

public class TimeQueryLog {

    /** @returns number of queries to host in last second. */
    public static int add(URI uri) {
        totalQueries++;
        QueryTime current = new QueryTime(uri);
        LinkedList<QueryTime> byHost = null;
        synchronized(recentQueries) {
            byHost = recentQueries.get(uri.getHost());
            if (byHost == null) {
                byHost = new LinkedList<QueryTime>();
                recentQueries.put(uri.getHost(), byHost);
            }
        }
        int numLastSec = 1; // start at one to include current query
        Instant secondAgo = Instant.now().minus(TimeUtils.ONE_SECOND);
        synchronized(byHost) {
            for (Iterator<QueryTime> iterator = byHost.iterator(); iterator.hasNext();) {
                QueryTime timeQuery = iterator.next();
                if (timeQuery.getWhen().isAfter(secondAgo)) {
                    numLastSec++;
                } else {
                    iterator.remove();
                }
            }
            byHost.add(current);
            if (numLastSec >= 10) {
                logger.warn("More than 10 queries in last second for "+uri.getHost()+"!!! " + numLastSec);
                List<QueryTime> toPrintList = byHost;
                if (numLastSec > 10) {
                    toPrintList = byHost.subList(0, 10);
                }
                for (QueryTime timeQuery : toPrintList) {
                    logger.warn("  " + timeQuery.getWhen() + "  " + timeQuery.getURI());
                }
                if (numLastSec > 10) {
                    logger.warn("...plus "+(numLastSec-10)+" more.");
                }
                logger.warn("Taking a little break to give the service time to catch its breath.");
                try {
                    synchronized(recentQueries) {
                        Thread.sleep(1000);
                    }
                } catch(InterruptedException e) {
                    // oh well....
                }
            }
            return numLastSec;
        }
    }

    static int totalQueries = 0;

    static HashMap<String, LinkedList<QueryTime>> recentQueries = new HashMap<String, LinkedList<QueryTime>>();

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TimeQueryLog.class);
}
