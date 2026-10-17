package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateTimeKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return parser.parseLocalDateTime(key);
        } catch (IllegalArgumentException e) {
            return (LocalDateTime) ctxt.handleWeirdKey(LocalDateTime.class, key,
                    "%s", e.getMessage());
        }
    }
}