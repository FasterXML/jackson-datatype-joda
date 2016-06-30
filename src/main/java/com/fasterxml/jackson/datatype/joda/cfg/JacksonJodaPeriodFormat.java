package com.fasterxml.jackson.datatype.joda.cfg;

import java.util.Locale;

import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Simple container used to encapsulate (some of) gory details of
 * customizations related to date/time formatting.
 */
public class JacksonJodaPeriodFormat extends JacksonJodaFormatBase
{
    protected final PeriodFormatter _formatter;

    public JacksonJodaPeriodFormat(PeriodFormatter defaultFormatter) {
        super();
        _formatter = defaultFormatter;
    }

    @Deprecated
    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Boolean useTimestamp)
    {
        super(base, useTimestamp);
        _formatter = base._formatter;
    }

    @Deprecated
    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base,
            PeriodFormatter formatter)
    {
        super(base);
        _formatter = formatter;
    }

    @Deprecated
    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Locale locale)
    {
        super(base, locale);
        _formatter = base._formatter.withLocale(locale);
    }

    public JacksonJodaPeriodFormat(PeriodFormatSetup setup) {
        super(setup);
        if(_explicitLocale) {
            _formatter = setup.getFormatter().withLocale(_locale);
        } else {
            _formatter = setup.getFormatter();
        }
    }

    @Override
    protected PeriodFormatSetup getSetup() {
        PeriodFormatSetup setup = new PeriodFormatSetup(super.getSetup());
        setup.setFormatter(_formatter);
        return setup;
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    public JacksonJodaPeriodFormat withUseTimestamp(Boolean useTimestamp) {
        if (_useTimestamp != null && _useTimestamp.equals(useTimestamp)) {
            return this;
        }
        PeriodFormatSetup setup = getSetup();
        setup.setUseTimeStamp(useTimestamp);
        return new JacksonJodaPeriodFormat(setup);
    }
    
    public JacksonJodaPeriodFormat withFormat(String format) {
        /* 17-Nov-2014, tatu: Does not look like there is all that much
         *   that can be customized... At most we might be able to
         *   use "alternate" variant?
         */
        return this;
    }

    public JacksonJodaPeriodFormat withLocale(Locale locale) {
        if ((locale == null) || (_locale != null && _locale.equals(locale))) {
            return this;
        }
        PeriodFormatSetup setup = getSetup();
        setup.setLocale(locale);
        return new JacksonJodaPeriodFormat(setup);
    }

    /*
    /**********************************************************
    /* Factory methods for other types
    /**********************************************************
     */

    public PeriodFormatter createFormatter(SerializerProvider provider)
    {
        PeriodFormatter formatter = _formatter;
        
        if (!_explicitLocale) {
            Locale loc = provider.getLocale();
            if (loc != null && !loc.equals(_locale)) {
                formatter = formatter.withLocale(loc);
            }
        }
        return formatter;
    }
}
