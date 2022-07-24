package tools.jackson.datatype.joda.cfg;

import java.io.IOException;
import java.util.Locale;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializerProvider;

/**
 * Simple container used to encapsulate (some of) gory details of
 * customizations related to date/time formatting.
 */
public class JacksonJodaPeriodFormat extends JacksonJodaFormatBase
{
    protected final PeriodFormatter _formatter;

    // Constructor called by FormatConfig for baseline defaults
    public JacksonJodaPeriodFormat(PeriodFormatter defaultFormatter) {
        super();
        _formatter = defaultFormatter;
    }

    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Locale locale)
    {
        super(base, locale);
        PeriodFormatter f = base._formatter;
        if (locale != null) {
            f = f.withLocale(locale);
        }
        _formatter = f;
    }

    public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Boolean useTimestamp)
    {
        super(base, useTimestamp);
        _formatter = base._formatter;
    }

    /**
     * @since 2.9
     */
    public PeriodFormatter nativeFormatter() {
        return _formatter;
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    // 30-Jun-2015, tatu: not 100% it's needed, but support for now...
    public JacksonJodaPeriodFormat withUseTimestamp(Boolean useTimestamp) {
        if (_useTimestamp != null && _useTimestamp.equals(useTimestamp)) {
            return this;
        }
        return new JacksonJodaPeriodFormat(this, useTimestamp);
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
        return new JacksonJodaPeriodFormat(this, locale);
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


    /**
     * @since 2.9
     */
    public Period parsePeriod(DeserializationContext ctxt, String str) throws IOException
    {
        return _formatter.parsePeriod(str);
    }
}
