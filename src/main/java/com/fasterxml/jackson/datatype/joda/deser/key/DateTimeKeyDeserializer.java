package com.fasterxml.jackson.datatype.joda.deser.key;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JacksonException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class DateTimeKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected DateTime deserialize(String key, DeserializationContext ctxt)
            throws JacksonException
    {
        if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
            return new DateTime(key, dtz);
        }
        return DateTime.parse(key);
    }

}
