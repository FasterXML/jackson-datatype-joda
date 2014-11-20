package com.fasterxml.jackson.datatype.joda.cfg;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 * Simple container class that holds both default formatter information
 * and explicit overrides caller has set for the Joda module to register.
 * Note that changes to configuration instance <b>MUST</b> be made before
 * registering the module; changes after registration may not have effect
 * on actual settings used.
 *
 * @since 2.5
 */
public class FormatConfig
{
    // Actual Joda formatters

    public final static DateTimeFormatter DEFAULT_JODA_DATEONLY_FORMAT
        = ISODateTimeFormat.date().withZoneUTC();

    public final static DateTimeFormatter DEFAULT_JODA_TIMEONLY_FORMAT
        = ISODateTimeFormat.time().withZoneUTC();

    public final static DateTimeFormatter DEFAULT_JODA_DATETIME_FORMAT
        = ISODateTimeFormat.dateTime().withZoneUTC();

    protected final static PeriodFormatter DEFAULT_JODA_PERIOD_FORMAT
        = ISOPeriodFormat.standard();
    
    // Matching wrappers for more information needed with formatter
    
    public final static JacksonJodaDateFormat DEFAULT_DATEONLY_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_DATEONLY_FORMAT);
    
    public final static JacksonJodaDateFormat DEFAULT_TIMEONLY_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_TIMEONLY_FORMAT);

    public final static JacksonJodaDateFormat DEFAULT_DATETIME_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_DATETIME_FORMAT);

    // should these differ from ones above? Presumably should use local timezone or... ?

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_DATETIME_FORMAT
                .withZone(DateTimeZone.getDefault())
                        );

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATEONLY_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_DATEONLY_FORMAT
             .withZone(DateTimeZone.getDefault())
                );

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_FORMAT
        = new JacksonJodaDateFormat(FormatConfig.DEFAULT_JODA_TIMEONLY_FORMAT
              .withZone(DateTimeZone.getDefault())
                );


    public final static JacksonJodaPeriodFormat DEFAULT_PERIOD_FORMAT
        = new JacksonJodaPeriodFormat(DEFAULT_JODA_PERIOD_FORMAT);
}
