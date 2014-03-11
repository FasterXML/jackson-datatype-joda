package com.fasterxml.jackson.datatype.joda.ser;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Simple container used to encapsulate (some of) gory details of
 * customizations related to date/time formatting.
 */
public class JacksonJodaFormat
{
    private final static String JODA_STYLE_CHARS = "SMLF-";

    protected final static Locale DEFAULT_LOCALE;
    static {
        DEFAULT_LOCALE = Locale.getDefault();
    }

    protected final DateTimeFormatter _formatter;
    
    /**
     * Flag that indicates that serialization must be done as the
     * Java timestamp, regardless of other settings.
     */
    protected final Boolean _useTimestamp;
    
    protected final TimeZone _jdkTimezone;

    protected final boolean _explicitTimezone;
    
    protected final Locale _locale;

    protected final boolean _explicitLocale;
    
    public JacksonJodaFormat(DateTimeFormatter defaultFormatter) {
        _useTimestamp = null;
        _jdkTimezone = defaultFormatter.getZone().toTimeZone();
        _locale = DEFAULT_LOCALE;
        _formatter = defaultFormatter;
        _explicitTimezone = false;
        _explicitLocale = false;
    }

    public JacksonJodaFormat(JacksonJodaFormat base, Boolean useTimestamp)
    {
        _useTimestamp = useTimestamp;
        _formatter = base._formatter;
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }
    
    public JacksonJodaFormat(JacksonJodaFormat base,
            DateTimeFormatter formatter)
    {
        _useTimestamp = base._useTimestamp;
        _formatter = formatter;
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    public JacksonJodaFormat(JacksonJodaFormat base, TimeZone jdkTimezone)
    {
        _useTimestamp = base._useTimestamp;
        _jdkTimezone = jdkTimezone;
        _explicitTimezone = true;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
        _formatter = base._formatter.withZone(DateTimeZone.forTimeZone(jdkTimezone));
    }

    public JacksonJodaFormat(JacksonJodaFormat base, Locale locale)
    {
        _useTimestamp = base._useTimestamp;
        _jdkTimezone = base._jdkTimezone;
        _explicitTimezone = base._explicitTimezone;
        _locale = locale;
        _explicitLocale = true;
        _formatter = base._formatter.withLocale(locale);
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    protected JacksonJodaFormat withUseTimestamp(Boolean useTimestamp) {
        if (_useTimestamp != null && _useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaFormat(this, useTimestamp);
    }
    
    protected JacksonJodaFormat withFormat(String format) {
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
        formatter = formatter.withZone(_formatter.getZone());
        return new JacksonJodaFormat(this, formatter);
    }
    
    protected JacksonJodaFormat withTimeZone(TimeZone tz) {
        if ((tz == null) || (_jdkTimezone != null && _jdkTimezone.equals(tz))) {
            return this;
        }
        return new JacksonJodaFormat(this, tz);
    }

    protected JacksonJodaFormat withLocale(Locale locale) {
        if ((locale == null) || (_locale != null && _locale.equals(locale))) {
            return this;
        }
        return new JacksonJodaFormat(this, locale);
    }

    /*
    /**********************************************************
    /* Factory methods for other types
    /**********************************************************
     */

    public boolean useTimestamp(SerializerProvider provider)
    {
        if (_useTimestamp != null) {
            return _useTimestamp.booleanValue();
        }
        return provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public DateTimeFormatter createFormatter(SerializerProvider provider)
    {
        DateTimeFormatter formatter = _formatter;
        
        if (!_explicitLocale) {
            Locale loc = provider.getLocale();
            if (loc != null && !loc.equals(_locale)) {
                formatter = formatter.withLocale(loc);
            }
        }
        if (!_explicitTimezone) {
            TimeZone tz = provider.getTimeZone();
            if (tz != null && !tz.equals(_jdkTimezone)) {
                formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
            }
        }
        
        return formatter;
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
}
