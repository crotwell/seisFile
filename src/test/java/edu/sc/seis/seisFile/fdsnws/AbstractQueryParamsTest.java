package edu.sc.seis.seisFile.fdsnws;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;


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
}
