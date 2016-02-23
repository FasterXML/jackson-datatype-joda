package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.Period;

import java.io.IOException;

public class PeriodKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected Period deserialize(String key, DeserializationContext ctxt) throws IOException {
        return Period.parse(key);
    }
}