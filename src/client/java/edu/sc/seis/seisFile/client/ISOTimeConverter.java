package edu.sc.seis.seisFile.client;

import java.time.Instant;

import edu.sc.seis.seisFile.ISOTimeParser;
import picocli.CommandLine.ITypeConverter;

public abstract class ISOTimeConverter extends ISOTimeParser implements ITypeConverter<Instant>{


    public ISOTimeConverter(boolean ceiling) {
        super(ceiling);
    }

    public Instant convert(String value) {
        return getDate(value);
    }
    
}
