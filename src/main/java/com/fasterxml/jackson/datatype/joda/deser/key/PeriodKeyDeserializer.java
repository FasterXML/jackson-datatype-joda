package com.fasterxml.jackson.datatype.joda.deser.key;

import org.joda.time.Period;

import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

public class PeriodKeyDeserializer extends JodaKeyDeserializer
{
    private static final long serialVersionUID = 1L;

    @Override
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key);
        } catch (IllegalArgumentException | ArithmeticException e) {
            // includes `NumberFormatException` for out-of-range components
            return ctxt.handleWeirdKey(Period.class, key,
                    "Invalid Period value: %s", e.getMessage());
        }
    }
}