package tools.jackson.datatype.joda.deser.key;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.cfg.DateTimeFeature;

public class DateTimeKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected DateTime deserialize(String key, DeserializationContext ctxt)
            throws JacksonException
    {
        DateTimeZone dtz = null;
        if (ctxt.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
        }
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return (dtz == null) ? DateTime.parse(key) : new DateTime(key, dtz);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return (DateTime) ctxt.handleWeirdKey(DateTime.class, key,
                    "%s", e.getMessage());
        }
    }

}
