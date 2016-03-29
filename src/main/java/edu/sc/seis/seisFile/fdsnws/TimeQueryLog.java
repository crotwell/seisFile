package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TimeQueryLog {

    public static void add(URI uri) {
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
        long secondAgo = new Date().getTime() - 1000;
        synchronized(byHost) {
            for (Iterator<QueryTime> iterator = byHost.iterator(); iterator.hasNext();) {
                QueryTime timeQuery = iterator.next();
                if (timeQuery.getWhen().getTime() > secondAgo) {
                    numLastSec++;
                } else {
                    iterator.remove();
                }
            }
            byHost.add(current);
            if (numLastSec >= 10) {
                logger.warn("More than 10 queries in last second for "+uri.getHost()+"!!! " + numLastSec);
                for (QueryTime timeQuery : byHost) {
                    logger.warn("  " + timeQuery.getWhen() + "  " + timeQuery.getURI());
                }
            }
        }
    }

    static int totalQueries = 0;

    static HashMap<String, LinkedList<QueryTime>> recentQueries = new HashMap<String, LinkedList<QueryTime>>();

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TimeQueryLog.class);
}
