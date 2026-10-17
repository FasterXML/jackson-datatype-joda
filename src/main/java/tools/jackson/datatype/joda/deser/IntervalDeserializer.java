package tools.jackson.datatype.joda.deser;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class IntervalDeserializer extends JodaDateDeserializerBase<Interval>
{
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
        throws JacksonException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getString());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
        }
        return (Interval) ctxt.handleUnexpectedToken(getValueType(ctxt),
                p.currentToken(), p, "expected JSON String");
    }

    // @since 2.12
    protected Interval _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }

        // 17-Nov-2014, tatu: Actually let's start with slash, instead of hyphen, because
        //   that is the separator for standard functionality...
        int index = value.indexOf('/', 1);
        boolean hasSlash = (index > 0);
        if (!hasSlash) {
            index = value.indexOf('-', 1);
        }
        if (index < 0) {
            // [datatype-joda#200]: report via handler (like other failures), not throw directly
            return (Interval) ctxt.handleWeirdStringValue(handledType(), value,
                    "no slash or hyphen found to separate start, end");
        }
        long start, end;
        String str = value.substring(0, index);
        Interval result;

        try {
            // !!! TODO: configurable formats...
            if (hasSlash) {
                result = Interval.parseWithOffset(value);
            } else {
                start = Long.parseLong(str);
                str = value.substring(index + 1);
                end = Long.parseLong(str);
                result = new Interval(start, end);
            }
        } catch (NumberFormatException e) {
            // [datatype-joda#200]: report full value (like other failures)
            return (Interval) ctxt.handleWeirdStringValue(handledType(), value,
                    "Failed to parse number from '%s'", str);
        } catch (IllegalArgumentException e) {
            // [datatype-joda#195]: do not let raw Joda exception escape
            // (invalid start/end, or end before start)
            return (Interval) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        } catch (ArithmeticException e) {
            // [datatype-joda#200]: overflow applying period/duration or offset
            return (Interval) ctxt.handleWeirdStringValue(handledType(), value,
                    "value out of range (overflow): %s", e.getMessage());
        }

        final DateTimeZone contextTimezone =
            _format.shouldAdjustToContextTimeZone(ctxt) ? _contextTimeZone(ctxt) : null;
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : contextTimezone;
        if (tz != null) {
            if (!tz.equals(result.getStart().getZone())) {
                result = new Interval(result.getStartMillis(), result.getEndMillis(), tz);
            }
        }
        return result;
    }
}
