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
            protected String getServiceName() {
                return "event";
            }
        };
        aqp.appendToParam(T, first);
        assertEquals("first", first, aqp.getParam(T));
        aqp.appendToParam(T, second);
        assertEquals("first", first+","+second, aqp.getParam(T));
        aqp.appendToParam(T, third);
        assertEquals("first", first+","+second+","+third, aqp.getParam(T));
    }
}
