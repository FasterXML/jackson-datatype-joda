package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.joda.time.Duration;

public class DurationKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key).toStandardDuration();
        } catch (IllegalArgumentException | ArithmeticException
                | UnsupportedOperationException e) {
            // [datatype-joda#195]: do not let raw Joda exception escape;
            // `UnsupportedOperationException` for Period with years/months
            return ctxt.handleWeirdKey(Duration.class, key,
                    "Invalid Duration value: %s", e.getMessage());
        }
    }
}
