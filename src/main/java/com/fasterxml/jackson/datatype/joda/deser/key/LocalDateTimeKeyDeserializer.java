package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class LocalDateTimeKeyDeserializer extends KeyDeserializer
{
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    @Override
    public LocalDateTime deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (key.length() == 0) { // [JACKSON-360]
            return null;
        }
        return parser.parseLocalDateTime(key);
    }
}