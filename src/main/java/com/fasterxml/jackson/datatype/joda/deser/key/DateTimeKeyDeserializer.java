package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class DateTimeKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected DateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        DateTimeZone dtz = null;
        if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
        }
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return (dtz == null) ? DateTime.parse(key) : new DateTime(key, dtz);
        } catch (IllegalArgumentException e) {
            return (DateTime) ctxt.handleWeirdKey(DateTime.class, key,
                    "%s", e.getMessage());
        }
    }

}
