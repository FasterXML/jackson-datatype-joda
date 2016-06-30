package com.fasterxml.jackson.datatype.joda.cfg;

import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

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

    @Deprecated
    protected JacksonJodaFormatBase() {
        _useTimestamp = null;
        _locale = DEFAULT_LOCALE;
        _explicitLocale = false;
    }

    @Deprecated
    protected JacksonJodaFormatBase(JacksonJodaFormatBase base)
    {
        _useTimestamp = base._useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    @Deprecated
    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Boolean useTimestamp)
    {
        _useTimestamp = useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    @Deprecated
    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, TimeZone jdkTimezone)
    {
        _useTimestamp = base._useTimestamp;
        _locale = base._locale;
        _explicitLocale = base._explicitLocale;
    }

    @Deprecated
    protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Locale locale)
    {
        _useTimestamp = base._useTimestamp;
        _locale = locale;
        _explicitLocale = true;
    }

    protected JacksonJodaFormatBase(BaseFormatSetup setup) {
        _useTimestamp = setup.isUseTimestamp();
        if (setup.getLocale() != null) {
            _locale = setup.getLocale();
            _explicitLocale = true;
        } else {
            _locale = DEFAULT_LOCALE;
            _explicitLocale = false;
        }
    }

    protected BaseFormatSetup getSetup() {
        BaseFormatSetup setup = new BaseFormatSetup();
        setup.setUseTimeStamp(_useTimestamp);
        if (_explicitLocale) {
            setup.setLocale(_locale);
        }
        return setup;
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
