package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class DateTimeKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected DateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
            return new DateTime(key, dtz);
        }
        return DateTime.parse(key);
    }

}
