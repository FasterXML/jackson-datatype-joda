package tools.jackson.datatype.joda.cfg;

import java.util.Locale;
import java.util.TimeZone;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;

/**
 * Base object for different formatters (date-time, period, ...)
 */
abstract class JacksonJodaFormatBase
{
    protected final static Locale DEFAULT_LOCALE;
    static {
        DEFAULT_LOCALE = Locale.getDefault();
    }

    /**
     * Flag that indicates that serialization must be done as the
     * Java timestamp, regardless of other settings.
     */
    protected final Boolean _useTimestamp;
    
    protected final Locale _locale;

    protected final boolean _explicitLocale;

    protected JacksonJodaFormatBase(Boolean useTimestamp,
            Locale locale, boolean explicitLocale) {
        _useTimestamp = useTimestamp;
        _locale = locale;
        _explicitLocale = explicitLocale;
    }

    protected JacksonJodaFormatBase() {
        _useTimestamp = null;
        _locale = DEFAULT_LOCALE;
        _explicitLocale = false;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base)
    {
        _useTimestamp = base._useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Boolean useTimestamp)
    {
        _useTimestamp = useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, TimeZone jdkTimezone)
    {
        _useTimestamp = base._useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Locale locale)
    {
        _useTimestamp = base._useTimestamp;
        if (locale == null) {
            _locale = DEFAULT_LOCALE;
            _explicitLocale = false;
        } else {
            _locale = locale;
            _explicitLocale = true;
        }
    }

    /*
    /**********************************************************
    /* Other public methods
    /**********************************************************
     */

    public boolean useTimestamp(SerializerProvider provider, SerializationFeature feat)
    {
        if (_useTimestamp != null) {
            return _useTimestamp.booleanValue();
        }
        return provider.isEnabled(feat);
    }
}
