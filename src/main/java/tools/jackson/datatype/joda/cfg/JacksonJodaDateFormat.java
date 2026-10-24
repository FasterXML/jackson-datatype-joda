package tools.jackson.datatype.joda.cfg;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParserBucket;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * Simple container used to encapsulate (some of) gory details of
 * customizations related to date/time formatting.
 */
public class JacksonJodaDateFormat extends JacksonJodaFormatBase
{
    private final static String JODA_STYLE_CHARS = "SMLF-";

    /**
     * Instants strictly within {@code (-2^62, 2^62)} (about +/- 146 million
     * years) cannot be result of {@code long} overflow; ones outside need
     * to be verified.
     */
    private final static long SAFE_INSTANT_RANGE = 1L << 62;

    protected final DateTimeFormatter _formatter;

    protected final DateTimeFormatter _formatterWithOffsetParsed;

    protected final TimeZone _jdkTimezone;

    protected transient DateTimeZone _jodaTimezone;
    
    protected final boolean _explicitTimezone;

    /**
     * Flag for <code>JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE</code>
     *
     * @since 2.8
     */
    protected final Boolean _adjustToContextTZOverride;

    /**
     * Flag for <code>JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID</code>
     *
     * @since 2.8
     */
    protected final Boolean _writeZoneId;
    
    public JacksonJodaDateFormat(DateTimeFormatter defaultFormatter)
    {
        super();
        _formatter = defaultFormatter;
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        DateTimeZone tz = defaultFormatter.getZone();
        _jdkTimezone = (tz == null) ? null : tz.toTimeZone();
        _explicitTimezone = false;
        _adjustToContextTZOverride = null;
        _writeZoneId = null;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base,
            Boolean useTimestamp)
    {
        super(base, useTimestamp);
        _formatter = base._formatter;
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
        _writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base,
            DateTimeFormatter formatter)
    {
        super(base);
        _formatter = formatter;
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
        _writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, TimeZone jdkTimezone)
    {
        super(base, jdkTimezone);
        _formatter = base._formatter.withZone(DateTimeZone.forTimeZone(jdkTimezone));
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        _jdkTimezone = jdkTimezone;
        _explicitTimezone = true;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
        _writeZoneId = base._writeZoneId;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, Locale locale)
    {
        super(base, locale);
        _formatter = base._formatter.withLocale(locale);
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
        _writeZoneId = base._writeZoneId;
    }

    /**
     * @since 2.8
     */
    protected JacksonJodaDateFormat(JacksonJodaDateFormat base,
            Boolean adjustToContextTZOverride, Boolean writeZoneId)
    {
        super(base);
        _formatter = base._formatter;
        _formatterWithOffsetParsed = _withOffsetParsed(_formatter);
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = adjustToContextTZOverride;
        _writeZoneId = writeZoneId;
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    public JacksonJodaDateFormat with(JsonFormat.Value ann) {
        JacksonJodaDateFormat format = this;
        format = format.withLocale(ann.getLocale());
        format = format.withTimeZone(ann.getTimeZone());
        format = format.withFormat(ann.getPattern());
        Boolean adjustTZ = ann.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        Boolean writeZoneId = ann.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);

        // `null` means no override specified, so only apply if override exists...
        // and changes current settings:
        if (((adjustTZ != null) && !adjustTZ.equals(_adjustToContextTZOverride))
                || ((writeZoneId != null) && !writeZoneId.equals(_writeZoneId))) {
            format = new JacksonJodaDateFormat(format, adjustTZ, writeZoneId);
        }
        return format;
    }

    public JacksonJodaDateFormat withUseTimestamp(Boolean useTimestamp) {
        if ((_useTimestamp != null) && _useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaDateFormat(this, useTimestamp);
    }
    
    public JacksonJodaDateFormat withFormat(String format) {
        if (format == null || format.isEmpty()) {
            return this;
        }
        DateTimeFormatter formatter;

        if (_isStyle(format)) {
            formatter = DateTimeFormat.forStyle(format);
        } else {
            formatter = DateTimeFormat.forPattern(format);
        }
        if (_locale != null) {
            formatter = formatter.withLocale(_locale);
        }

        // [datatype-joda#98] Since 2.19.1, fix `@JsonFormat.timezone` not taking effect
        // [If a timezone was explicitly set earlier, retain it on the new formatter
        if (_explicitTimezone && _jdkTimezone != null) {
            formatter = formatter.withZone(DateTimeZone.forTimeZone(_jdkTimezone));
        }
        return new JacksonJodaDateFormat(this, formatter);
    }

    public JacksonJodaDateFormat withTimeZone(TimeZone tz) {
        // Must not short-circuit on non-explicit default zone, even if it is the same
        // (otherwise annotation-specified zone that equals JVM default is not "explicit")
        if ((tz == null)
                || (_explicitTimezone && _jdkTimezone != null && _jdkTimezone.equals(tz))) {
            return this;
        }
        return new JacksonJodaDateFormat(this, tz);
    }

    public JacksonJodaDateFormat withLocale(Locale locale) {
        if ((locale == null) || (_locale != null && _locale.equals(locale))) {
            return this;
        }
        return new JacksonJodaDateFormat(this, locale);
    }

    /**
     * @since 2.8
     */
    public JacksonJodaDateFormat withAdjustToContextTZOverride(Boolean adjustToContextTZOverride) {
        // minor efficiency check to avoid recreation if no change:
        if (adjustToContextTZOverride == _adjustToContextTZOverride) {
            return this;
        }
        return new JacksonJodaDateFormat(this, adjustToContextTZOverride, _writeZoneId);
    }

    /**
     * @since 2.8
     */
    public JacksonJodaDateFormat withWriteZoneId(Boolean writeZoneId) {
        // minor efficiency check to avoid recreation if no change:
        if (writeZoneId == _writeZoneId) {
            return this;
        }
        return new JacksonJodaDateFormat(this, _adjustToContextTZOverride, writeZoneId);
    }

    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */

    /**
     * @since 2.6
     */
    public DateTimeZone getTimeZone() {
        if (_jodaTimezone != null) {
            return _jodaTimezone;
        }
        if (_jdkTimezone == null) {
            return null;
        }
        DateTimeZone tz = DateTimeZone.forTimeZone(_jdkTimezone);
        _jodaTimezone = tz;
        return tz;
    }

    public Locale getLocale() {
        return _locale;
    }

    /*
    /**********************************************************
    /* Factory methods for other types
    /**********************************************************
     */

    public DateTimeFormatter rawFormatter() {
        return _formatter;
    }

    /**
     * Creates a formatter with settings this format object has.
     */
    public DateTimeFormatter createFormatter(SerializationContext ctxt)
    {
        return createFormatterWithTimeZoneOverride(ctxt, null);
    }

    /**
     * Creates a formatter with the specified timezone from the value if any.
     *
     * [dataformat-joda#92] DateTime serialization result is not same as Java 8 ZonedDateTime
     */
    public DateTimeFormatter createFormatterWithTimeZoneOverride(SerializationContext ctxt, DateTimeZone tzOverride)
    {
        DateTimeFormatter formatter = createFormatterWithLocale(ctxt);
        if (!_explicitTimezone) {
            TimeZone tz = ctxt.getTimeZone();
            if ((tz != null) && !tz.equals(_jdkTimezone)) {
                formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
            }
        }
        if (tzOverride != null) {
            if (!ctxt.isEnabled(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)) {
                if ((_jdkTimezone == null) || !tzOverride.toTimeZone().equals(_jdkTimezone)) {
                    formatter = formatter.withZone(tzOverride);
                }
            }
        }
        return formatter;
    }

    public DateTimeFormatter createFormatterWithLocale(SerializationContext ctxt)
    {
        DateTimeFormatter formatter = _formatter;
        if (!_explicitLocale) {
            Locale loc = ctxt.getLocale();
            if (loc != null && !loc.equals(_locale)) {
                formatter = formatter.withLocale(loc);
            }
        }
        return formatter;
    }

    /**
     * Accessor used during deserialization.
     */
    public DateTimeFormatter createParser(DeserializationContext ctxt)
    {
        DateTimeFormatter formatter = _formatter;
        if (!_explicitTimezone) {
            final boolean adjust = shouldAdjustToContextTimeZone(ctxt);
            if (!adjust) {
                formatter = _formatterWithOffsetParsed;
            }
            final TimeZone tz = ctxt.getTimeZone();
            if (tz != null && !tz.equals(_jdkTimezone)) {
                final DateTimeZone dtz = DateTimeZone.forTimeZone(tz);
                // 20-Oct-2026: when not adjusting, values without offset must still be
                //   resolved using context time zone (not JVM default one); offset-parsing
                //   formatter ignores zone override so pass it via chronology
                formatter = adjust ? formatter.withZone(dtz)
                        : _withZoneViaChronology(formatter, dtz);
            }
        }
        if (!_explicitLocale) {
            Locale loc = ctxt.getLocale();
            if (loc != null && !loc.equals(_locale)) {
                formatter = formatter.withLocale(loc);
            }
        }
        return formatter;
    }

    /**
     * Helper method for parsing given text as {@link DateTime} using given parser,
     * verifying that the instant did not overflow: Joda accepts years one past
     * the supported range (like {@code 292278994}) but does not check
     * calculation of the instant, which silently wraps around to the other end
     * of the timeline for values past {@code Long.MAX_VALUE} (or before
     * {@code Long.MIN_VALUE}).
     *
     * @throws IllegalArgumentException if text is not valid for parser
     * @throws ArithmeticException if the value is out of supported range
     *
     * @since 3.3
     */
    public static DateTime parseDateTime(DateTimeFormatter parser, String text)
    {
        final DateTime result = parser.parseDateTime(text);
        final long millis = result.getMillis();
        if (millis <= -SAFE_INSTANT_RANGE || millis >= SAFE_INSTANT_RANGE) {
            // Overflow flips the sign of instant (relative to that of the year)
            final YearCapture bucket = new YearCapture(parser);
            parser.getParser().parseInto(bucket, text, 0);
            if ((bucket.year != null) && ((bucket.year < 0) != (millis < 0))) {
                throw new ArithmeticException(String.format(
                        "Cannot parse \"%s\": instant out of supported range (overflow)", text));
            }
        }
        return result;
    }

    /**
     * @since 2.8
     */
    public boolean shouldAdjustToContextTimeZone(DeserializationContext ctxt) {
        return (_adjustToContextTZOverride != null) ? _adjustToContextTZOverride :
            ctxt.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    /**
     * @since 2.8
     */
    public boolean shouldWriteWithZoneId(SerializationContext ctxt) {
        return (_writeZoneId != null) ? _writeZoneId :
            ctxt.isEnabled(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    /**
     * Differentiate if TimeZone is specified by caller 
     * @return true if TimeZone is specified by caller; false otherwise.
     */
    public boolean isTimezoneExplicit() {
        return _explicitTimezone;
    }
    
    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    /**
     * Helper method for creating a parser that retains the offset parsed from
     * the value but still resolves values without offset using the time zone
     * of the given formatter (and not the JVM default one):
     * {@link DateTimeFormatter#withOffsetParsed} clears the zone override so
     * it has to be passed via the chronology instead.
     *
     * @since 3.3
     */
    private static DateTimeFormatter _withOffsetParsed(DateTimeFormatter formatter) {
        final DateTimeZone tz = formatter.getZone();
        formatter = formatter.withOffsetParsed();
        if (tz != null) {
            formatter = _withZoneViaChronology(formatter, tz);
        }
        return formatter;
    }

    private static DateTimeFormatter _withZoneViaChronology(DateTimeFormatter formatter,
            DateTimeZone tz) {
        return formatter.withChronology(DateTimeUtils.getChronology(formatter.getChronology())
                .withZone(tz));
    }

    /**
     * Bucket used for finding out the year (or weekyear) parsed from the value,
     * to detect overflow of the instant calculated from it.
     */
    private static final class YearCapture extends DateTimeParserBucket
    {
        Integer year;

        YearCapture(DateTimeFormatter parser) {
            super(0L, ISOChronology.getInstanceUTC(), parser.getLocale(),
                    parser.getPivotYear(), parser.getDefaultYear());
        }

        @Override
        public void saveField(DateTimeFieldType fieldType, int value) {
            if (fieldType == DateTimeFieldType.year()
                    || fieldType == DateTimeFieldType.weekyear()) {
                year = value;
            }
            super.saveField(fieldType, value);
        }

        @Override
        public void saveField(DateTimeField field, int value) {
            saveField(field.getType(), value);
        }
    }

    protected static boolean _isStyle(String formatStr) {
        if (formatStr.length() != 2) {
            return false;
        }
        return (JODA_STYLE_CHARS.indexOf(formatStr.charAt(0)) >= 0)
                && (JODA_STYLE_CHARS.indexOf(formatStr.charAt(0)) >= 0);
    }

    @Override
    public String toString() {
        return String.format("[JacksonJodaFormat, explicitTZ? %s, JDK tz = %s, formatter = %s]",
                _explicitTimezone, _jdkTimezone.getID(), _formatter);
    }
}
