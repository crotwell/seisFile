package edu.sc.seis.seisFile.client;

import java.util.Map;

import edu.sc.seis.seisFile.DonutArea;
import picocli.CommandLine.ITypeConverter;

public class DonutParser extends PatternParser implements ITypeConverter<DonutArea> {

    public DonutArea convert(String value) {
        Map<String, String> m = parse(value);
        DonutArea donut = new  DonutArea();
        donut.latitude = Float.parseFloat(m.get("lat"));
        donut.longitude = Float.parseFloat(m.get("lon"));
        donut.minradius = Float.parseFloat(m.get("min"));
        donut.maxradius = Float.parseFloat(m.get("max"));
        return donut;
    }
    
    
    public DonutParser() {
        super(BoxAreaParser.FOUR_SLASH_DELIMITED_DECIMALS_RE,
              new String[] {"lat", "lon", "min", "max"});
    }

    public String getErrorMessage(String arg) {
        return "A donut is specified as centerLat/centerLon/minRadiusDegrees/maxRadiusDegrees not '"
                + arg + "'";
    }
    
    public static final String NAME = "donut";
}
