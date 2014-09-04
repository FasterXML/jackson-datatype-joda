package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class LocalDateTimeKeyDeserializer extends JodaKeyDeserializer {
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return parser.parseLocalDateTime(key);
    }
}