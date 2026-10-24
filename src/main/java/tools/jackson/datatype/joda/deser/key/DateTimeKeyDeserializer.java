package tools.jackson.datatype.joda.deser.key;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class DateTimeKeyDeserializer extends JodaKeyDeserializer
{
    // Same parser as used by `DateTime.parse(String)`
    private static final DateTimeFormatter PARSER = ISODateTimeFormat.dateTimeParser()
            .withOffsetParsed();

    @Override
    protected DateTime deserialize(String key, DeserializationContext ctxt)
            throws JacksonException
    {
        TimeZone tz = ctxt.getTimeZone();
        final DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
        if (ctxt.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            // 24-Oct-2026: same as `new DateTime(key, dtz)` but verifies
            //   that the instant did not overflow
            final DateTimeFormatter parser = PARSER.withZone(dtz);
            return _parseKey(ctxt, DateTime.class, key,
                    k -> JacksonJodaDateFormat.parseDateTime(parser, k));
        }
        // 20-Oct-2026: key without offset must be resolved using context
        //   time zone (not JVM default one); offset is retained if included
        final DateTimeFormatter parser = PARSER.withChronology(ISOChronology.getInstance(dtz));
        return _parseKey(ctxt, DateTime.class, key,
                k -> JacksonJodaDateFormat.parseDateTime(parser, k));
    }

}
