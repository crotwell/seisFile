package edu.sc.seis.seisFile.client;

import java.time.Instant;

import picocli.CommandLine.ITypeConverter;

public abstract class ISOTimeConverter extends ISOTimeParser implements ITypeConverter<Instant>{


    public ISOTimeConverter(boolean ceiling) {
        super(ceiling);
    }

    public Instant convert(String value) throws Exception {
        return getDate(value);
    }
    
}
