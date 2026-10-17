package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalTimeKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter parser = ISODateTimeFormat.localTimeParser();

    @Override
    protected LocalTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return parser.parseLocalTime(key);
        } catch (IllegalArgumentException e) {
            return (LocalTime) ctxt.handleWeirdKey(LocalTime.class, key,
                    "%s", e.getMessage());
        }
    }
}