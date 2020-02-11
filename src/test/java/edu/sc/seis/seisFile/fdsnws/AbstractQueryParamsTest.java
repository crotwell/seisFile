package edu.sc.seis.seisFile.fdsnws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;


import edu.sc.seis.seisFile.TimeUtils;


public class AbstractQueryParamsTest {

    @Test
    public void testAppendToParam() throws URISyntaxException {
        String T = "TEST";
        String first = "first";
        String second = "second";
        String third = "third";
        AbstractQueryParams aqp = new AbstractQueryParams("test.seis.sc.edu") {
            @Override
            public String getServiceName() {
                return "event";
            }
        };
        aqp.appendToParam(T, first);
        assertEquals("first", first, aqp.getParam(T));
        aqp.appendToParam(T, second);
        assertEquals("second", first+","+second, aqp.getParam(T));
        aqp.appendToParam(T, third);
        assertEquals("third", first+","+second+","+third, aqp.getParam(T));
    }
    
    @Test
    public void testRepeatAppendToParam() throws URISyntaxException {
        String T = "TEST";
        String first = "first";
        String second = "second";
        String third = "third";
        AbstractQueryParams aqp = new AbstractQueryParams("test.seis.sc.edu") {
            @Override
            public String getServiceName() {
                return "event";
            }
        };
        aqp.appendToParam(T, first);
        assertEquals("first", first, aqp.getParam(T));
        aqp.appendToParam(T, second);
        assertEquals("second", first+","+second, aqp.getParam(T));
        aqp.appendToParam(T, third);
        assertEquals("third", first+","+second+","+third, aqp.getParam(T));
        // repeat, so not appended
        String expected = first+","+second+","+third;
        aqp.appendToParam(T, first);
        assertEquals("repeat first", expected, aqp.getParam(T));
        aqp.appendToParam(T, second);
        assertEquals("repeat first", expected, aqp.getParam(T));
        aqp.appendToParam(T, third);
        assertEquals("repeat first", expected, aqp.getParam(T));
    }

    
    @Test
    public void testInstantSetParam() throws URISyntaxException {
        String T = "TEST";
        AbstractQueryParams aqp = new AbstractQueryParams("test.seis.sc.edu") {
            @Override
            public String getServiceName() {
                return "event";
            }
        };
        Instant time = TimeUtils.parseISOString("2013-03-15T12:35:21Z");
        aqp.setParam(T, time);
        System.out.println("Q: "+aqp.formURI());
        assertEquals("time", time.toString(), aqp.getParam(T));
    }
}
