package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

public class PeriodKeyDeserializer extends JodaKeyDeserializer
{
    private static final long serialVersionUID = 1L;

    @Override
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        return PERIOD_FORMAT.parsePeriod(ctxt, key);
    }
}