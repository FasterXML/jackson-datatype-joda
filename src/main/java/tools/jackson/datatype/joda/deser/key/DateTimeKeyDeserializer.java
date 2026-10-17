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
        if (ctxt.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            final DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
            return _parseKey(ctxt, DateTime.class, key, k -> new DateTime(k, dtz));
        }
        return _parseKey(ctxt, DateTime.class, key, DateTime::parse);
    }

}
