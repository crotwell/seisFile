package edu.sc.seis.seisFile.client;

import java.util.Map;

import edu.sc.seis.seisFile.BoxArea;
import picocli.CommandLine.ITypeConverter;

public class BoxAreaParser extends PatternParser implements ITypeConverter<BoxArea> {

    public BoxArea convert(String value) throws Exception {
        Map<String, String> m = parse(value);
        BoxArea box = new  BoxArea();
        box.west = Float.parseFloat(m.get("west"));
        box.east = Float.parseFloat(m.get("east"));
        box.north = Float.parseFloat(m.get("north"));
        box.south = Float.parseFloat(m.get("south"));
        return box;
    }
    
    
    public BoxAreaParser() {
        super(FOUR_SLASH_DELIMITED_DECIMALS_RE, new String[] {"west",
                                                              "east",
                                                              "south",
                                                              "north"});
    }

    public String getErrorMessage(String arg) {
        return "A box area is specified as its edges separated by slashes, west/east/south/north, not '"
                + arg + "'";
    }

    public static final String NAME = "box";
    
    public static final String DECIMAL_NUMBER_RE = "(-?\\d+\\.?\\d*)";

    public static final String FOUR_SLASH_DELIMITED_DECIMALS_RE = DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE;
}