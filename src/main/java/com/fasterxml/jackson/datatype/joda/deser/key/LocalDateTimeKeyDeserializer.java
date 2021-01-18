package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateTimeKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return parser.parseLocalDateTime(key);
    }
}