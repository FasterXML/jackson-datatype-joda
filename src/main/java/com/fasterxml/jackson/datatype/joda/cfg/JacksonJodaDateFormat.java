package com.fasterxml.jackson.datatype.joda.cfg;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Simple container used to encapsulate (some of) gory details of
 * customizations related to date/time formatting.
 */
public class JacksonJodaDateFormat extends JacksonJodaFormatBase
{
    private final static String JODA_STYLE_CHARS = "SMLF-";

    protected final DateTimeFormatter _formatter;

    protected final TimeZone _jdkTimezone;

    protected transient DateTimeZone _jodaTimezone;
    
    protected final boolean _explicitTimezone;

    protected final Boolean _adjustToContextTZOverride;

    public JacksonJodaDateFormat(DateTimeFormatter defaultFormatter)
    {
        super();
        _formatter = defaultFormatter;
        DateTimeZone tz = defaultFormatter.getZone();
        _jdkTimezone = (tz == null) ? null : tz.toTimeZone();
        _explicitTimezone = false;
        _adjustToContextTZOverride = null;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base,
            Boolean useTimestamp, Boolean adjustToContextTZOverride)
    {
        super(base, useTimestamp);
        _formatter = base._formatter;
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = adjustToContextTZOverride;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base,
            DateTimeFormatter formatter)
    {
        super(base);
        _formatter = formatter;
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, TimeZone jdkTimezone)
    {
        super(base, jdkTimezone);
        _formatter = base._formatter.withZone(DateTimeZone.forTimeZone(jdkTimezone));
        _jdkTimezone = jdkTimezone;
        _explicitTimezone = true;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    public JacksonJodaDateFormat(JacksonJodaDateFormat base, Locale locale)
    {
        super(base, locale);
        _formatter = base._formatter.withLocale(locale);
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    public JacksonJodaDateFormat withUseTimestamp(Boolean useTimestamp) {
        if ((_useTimestamp != null) && _useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaDateFormat(this, useTimestamp, _adjustToContextTZOverride);
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
        return new JacksonJodaDateFormat(this, formatter);
    }

    public JacksonJodaDateFormat withTimeZone(TimeZone tz) {
        if ((tz == null) || (_jdkTimezone != null && _jdkTimezone.equals(tz))) {
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

    public JacksonJodaDateFormat withAdjustToContextTZOverride(Boolean adjustToContextTZOverride) {
        // minor efficiency check to avoid recreation if no change:
        if (adjustToContextTZOverride == _adjustToContextTZOverride) {
            return this;
        }
        return new JacksonJodaDateFormat(this, _useTimestamp, adjustToContextTZOverride);
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

    public DateTimeFormatter createFormatter(SerializerProvider ctxt)
    {
        DateTimeFormatter formatter = createFormatterWithLocale(ctxt);
        if (!_explicitTimezone) {
            TimeZone tz = ctxt.getTimeZone();
            if ((tz != null) && !tz.equals(_jdkTimezone)) {
                formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
            }
        }
        return formatter;
    }

    public DateTimeFormatter createFormatterWithLocale(SerializerProvider ctxt)
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
        if (!_explicitLocale) {
            Locale loc = ctxt.getLocale();
            if (loc != null && !loc.equals(_locale)) {
                formatter = formatter.withLocale(loc);
            }
        }
        if (!_explicitTimezone) {
            if (isAdjustDatesToContextTimeZone(ctxt)) {
                TimeZone tz = ctxt.getTimeZone();
                if (tz != null && !tz.equals(_jdkTimezone)) {
                    formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
                }
            } else {
                formatter = formatter.withOffsetParsed();
            }
        }
        return formatter;
    }

    private boolean isAdjustDatesToContextTimeZone(DeserializationContext ctxt) {
      return (_adjustToContextTZOverride != null) ? _adjustToContextTZOverride :
              ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
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
