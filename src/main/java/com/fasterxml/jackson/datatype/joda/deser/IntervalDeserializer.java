package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class IntervalDeserializer extends JodaDateDeserializerBase<Interval>
{
    private static final long serialVersionUID = 1L;

    public IntervalDeserializer() {
        // NOTE: not currently used, but must pass something
        this(FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    public IntervalDeserializer(JacksonJodaDateFormat format) {
        super(Interval.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new IntervalDeserializer(format);
    }

    @Override
    public Interval deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            return (Interval) ctxt.handleUnexpectedToken(handledType(),
                    p.getCurrentToken(), p, "expected JSON String");
        }

        String v = p.getText().trim();

        /* 17-Nov-2014, tatu: Actually let's start with slash, instead of hyphen, because
         *   that is the separator for standard functionality...
         */
        int index = v.indexOf('/', 1);
        boolean hasSlash = (index > 0);
        if (!hasSlash) {
            index = v.indexOf('-', 1);
        }
        if (index < 0) {
            throw ctxt.weirdStringException(v, handledType(), "no slash or hyphen found to separate start, end");
        }
        long start, end;
        String str = v.substring(0, index);
        Interval result;

        try {
            // !!! TODO: configurable formats...
            if (hasSlash) {
                result = Interval.parse(v);
            } else {
                start = Long.valueOf(str);
                str = v.substring(index + 1);
                end = Long.valueOf(str);
                result = new Interval(start, end);
            }
        } catch (NumberFormatException e) {
            return (Interval) ctxt.handleWeirdStringValue(handledType(), str,
"Failed to parse number from '%s' (full source String '%s')",
                    str, v);
        }

        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        if (tz != null) {
            if (!tz.equals(result.getStart().getZone())) {
                result = new Interval(result.getStartMillis(), result.getEndMillis(), tz);
            }
        }
        return result;
    }
}
