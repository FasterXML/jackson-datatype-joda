package tools.jackson.datatype.joda.deser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class IntervalDeserializer extends JodaDateDeserializerBase<Interval>
{
    // Same parsers as used by `Interval.parseWithOffset()`
    private static final DateTimeFormatter ISO_DATETIME_PARSER = ISODateTimeFormat.dateTimeParser()
            .withOffsetParsed();
    private static final PeriodFormatter ISO_PERIOD_PARSER = ISOPeriodFormat.standard()
            .withParseType(PeriodType.standard());

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
                result = _fromISOString(ctxt, value, index);
            } else {
                start = Long.parseLong(str);
                str = value.substring(index + 1);
                end = Long.parseLong(str);
                // 20-Oct-2026: instants are zone-independent, but use same zone as for
                //   ISO form (not JVM default one) for consistent chronology of result
                result = new Interval(start, end, _intervalZone(ctxt));
            }
        } catch (NumberFormatException e) {
            // [datatype-joda#200]: report full value (like other failures)
            return (Interval) _handleWeirdStringValue(ctxt, e, value,
                    "Failed to parse number from '%s'", str);
        } catch (IllegalArgumentException e) {
            // [datatype-joda#195]: do not let raw Joda exception escape
            // (invalid start/end, or end before start)
            return (Interval) _handleWeirdStringValue(ctxt, e, value,
                    "%s", e.getMessage());
        } catch (ArithmeticException e) {
            // [datatype-joda#200]: overflow applying period/duration or offset
            return (Interval) _handleWeirdStringValue(ctxt, e, value,
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

    /**
     * Helper method for parsing ISO-8601 interval notation ({@code start/end},
     * {@code start/period} or {@code period/end}), same as
     * {@link Interval#parseWithOffset} except that date/time values without
     * offset are resolved using the configured time zone (explicit format time
     * zone if any, context time zone otherwise) and not the JVM default one.
     *
     * @since 3.3
     */
    protected Interval _fromISOString(DeserializationContext ctxt, String value, int separator)
    {
        final String leftStr = value.substring(0, separator);
        final String rightStr = value.substring(separator + 1);
        // `withOffsetParsed()` clears zone override so zone has to be passed via chronology
        final DateTimeFormatter parser = ISO_DATETIME_PARSER
                .withChronology(ISOChronology.getInstance(_intervalZone(ctxt)));
        DateTime start = null;
        Period period = null;

        if (_isPeriod(leftStr)) {
            period = ISO_PERIOD_PARSER.parsePeriod(leftStr);
        } else {
            start = JacksonJodaDateFormat.parseDateTime(parser, leftStr);
        }
        if (_isPeriod(rightStr)) {
            if (period != null) {
                throw new IllegalArgumentException("Interval composed of two durations: " + value);
            }
            return new Interval(start, ISO_PERIOD_PARSER.parsePeriod(rightStr));
        }
        final DateTime end = JacksonJodaDateFormat.parseDateTime(parser, rightStr);
        if (period != null) {
            return new Interval(period, end);
        }
        return new Interval(start, end);
    }

    /**
     * Time zone used for values without offset and for chronology of results
     * of both String forms: explicit format time zone if any, context time zone
     * otherwise (regardless of whether adjustment to context zone is enabled).
     */
    private DateTimeZone _intervalZone(DeserializationContext ctxt) {
        return _format.isTimezoneExplicit() ? _format.getTimeZone() : _contextTimeZone(ctxt);
    }

    private static boolean _isPeriod(String str) {
        if (str.isEmpty()) {
            return false;
        }
        final char c = str.charAt(0);
        return (c == 'P') || (c == 'p');
    }
}
