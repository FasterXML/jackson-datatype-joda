package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JacksonException;

import com.fasterxml.jackson.databind.DeserializationContext;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateParser();

    @Override
    protected LocalDate deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return parser.parseLocalDate(key);
    }
}