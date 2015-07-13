package com.fasterxml.jackson.datatype.joda.cfg;

import org.joda.time.DateTimeZone;
import org.joda.time.format.*;

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
    // Matching wrappers for more information needed with formatter
    
    public final static JacksonJodaDateFormat DEFAULT_DATEONLY_FORMAT
        = new JacksonJodaDateFormat(ISODateTimeFormat.date().withZoneUTC());
    
    public final static JacksonJodaDateFormat DEFAULT_TIMEONLY_FORMAT
        = new JacksonJodaDateFormat(ISODateTimeFormat.time().withZoneUTC());

    public final static JacksonJodaDateFormat DEFAULT_DATETIME_FORMAT
        = new JacksonJodaDateFormat(ISODateTimeFormat.dateTime().withZoneUTC());

    public final static JacksonJodaPeriodFormat DEFAULT_PERIOD_FORMAT
        = new JacksonJodaPeriodFormat(ISOPeriodFormat.standard());
    
    // should these differ from ones above? Presumably should use local timezone or... ?

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATEONLY_FORMAT
        = new JacksonJodaDateFormat(ISODateTimeFormat.date()
             .withZone(DateTimeZone.getDefault()));

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PRINTER
        = new JacksonJodaDateFormat(ISODateTimeFormat.time()
              .withZone(DateTimeZone.getDefault()));

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PARSER
        = new JacksonJodaDateFormat(ISODateTimeFormat.localTimeParser()
              .withZone(DateTimeZone.getDefault()));
    
    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PRINTER
        = new JacksonJodaDateFormat(ISODateTimeFormat.dateTime()
                .withZone(DateTimeZone.getDefault()));

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PARSER
        = new JacksonJodaDateFormat(ISODateTimeFormat.localDateOptionalTimeParser()
                .withZone(DateTimeZone.getDefault()));
}
