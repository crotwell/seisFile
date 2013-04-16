package edu.sc.seis.seisFile.client;

import com.martiansoftware.jsap.FlaggedOption;

public class BoxAreaParser extends PatternParser {

    public BoxAreaParser() {
        super(FOUR_SLASH_DELIMITED_DECIMALS_RE, new String[] {"west",
                                                              "east",
                                                              "south",
                                                              "north"});
    }

    public static FlaggedOption createParam(String helpMessage) {
        return new FlaggedOption("box",
                                 new BoxAreaParser(),
                                 null,
                                 false,
                                 'R',
                                 "box-area",
                                 helpMessage);
    }

    public String getErrorMessage(String arg) {
        return "A box area is specified as its edges separated by slashes, west/east/south/north, not '"
                + arg + "'";
    }

    public static final String DECIMAL_NUMBER_RE = "(-?\\d+\\.?\\d*)";

    public static final String FOUR_SLASH_DELIMITED_DECIMALS_RE = DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE
            + "/"
            + DECIMAL_NUMBER_RE;
}