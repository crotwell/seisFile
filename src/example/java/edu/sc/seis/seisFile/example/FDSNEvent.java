package edu.sc.seis.seisFile.example;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import edu.sc.seis.seisFile.fdsnws.FDSNEventQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQueryParams;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;

public class FDSNEvent {

    public void run() {
        try {
            FDSNEventQueryParams queryParams = new FDSNEventQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryParams.area(30, 35, -83, -79)
                    .setStartTime(sdf.parse("2010-03-15"))
                    .setEndTime(sdf.parse("2013-03-21"))
                    .setMaxDepth(100)
                    .setMinMagnitude(1)
                    .setOrderBy(FDSNEventQueryParams.ORDER_TIME_ASC);
            FDSNEventQuerier querier = new FDSNEventQuerier(queryParams); 
            Quakeml quakeml = querier.getQuakeML();
            if (!quakeml.checkSchemaVersion()) {
                System.out.println("");
                System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                System.out.println("XmlSchema (doc): " + quakeml.getSchemaVersion());
            }
            EventIterator eIt = quakeml.getEventParameters().getEvents();
            while (eIt.hasNext()) {
                Event e = eIt.next();
                Origin o = e.getOriginList().get(0);
                Magnitude m = e.getMagnitudeList().get(0);
                System.out.println(o.getLatitude()+"/"+o.getLongitude()+" "+m.getMag().getValue()+" "+m.getType()+" "+o.getTime().getValue());
            }
        } catch(Exception e) {
            System.err.println("Oops: " + e.getMessage());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new FDSNEvent().run();
    }
}
