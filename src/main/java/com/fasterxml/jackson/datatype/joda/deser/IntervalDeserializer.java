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
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        return (Interval) ctxt.handleUnexpectedToken(getValueType(ctxt),
                p.currentToken(), p, "expected JSON String");
    }

    // @since 2.12
    protected Interval _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return getNullValue(ctxt);
        }

        // 17-Nov-2014, tatu: Actually let's start with slash, instead of hyphen, because
        //   that is the separator for standard functionality...
        int index = value.indexOf('/', 1);
        boolean hasSlash = (index > 0);
        if (!hasSlash) {
            index = value.indexOf('-', 1);
        }
        if (index < 0) {
            throw ctxt.weirdStringException(value, handledType(), "no slash or hyphen found to separate start, end");
        }
        long start, end;
        String str = value.substring(0, index);
        Interval result;

        try {
            // !!! TODO: configurable formats...
            if (hasSlash) {
                result = Interval.parseWithOffset(value);
            } else {
                start = Long.valueOf(str);
                str = value.substring(index + 1);
                end = Long.valueOf(str);
                result = new Interval(start, end);
            }
        } catch (NumberFormatException e) {
            return (Interval) ctxt.handleWeirdStringValue(handledType(), str,
"Failed to parse number from '%s' (full source String '%s')",
                    str, value);
        }

        final DateTimeZone contextTimezone =
            _format.shouldAdjustToContextTimeZone(ctxt) ? DateTimeZone.forTimeZone(ctxt.getTimeZone()) : null;
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : contextTimezone;
        if (tz != null) {
            if (!tz.equals(result.getStart().getZone())) {
                result = new Interval(result.getStartMillis(), result.getEndMillis(), tz);
            }
        }
        return result;
    }
}
