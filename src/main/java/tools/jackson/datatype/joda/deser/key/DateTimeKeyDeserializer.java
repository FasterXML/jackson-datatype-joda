package tools.jackson.datatype.joda.deser.key;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;

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
