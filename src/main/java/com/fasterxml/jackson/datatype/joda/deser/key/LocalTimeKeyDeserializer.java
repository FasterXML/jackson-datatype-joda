package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalTimeKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localTimeParser();

    @Override
    protected LocalTime deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return parser.parseLocalTime(key);
    }
}