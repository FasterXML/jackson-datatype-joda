package tools.jackson.datatype.joda.cfg;

import java.util.Arrays;

import org.joda.time.DateTimeZone;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;

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
    private final static DateTimeZone DEFAULT_TZ = DateTimeZone.getDefault();

    // Matching wrappers for more information needed with formatter
    
    public final static JacksonJodaDateFormat DEFAULT_DATEONLY_FORMAT
        = createUTC(ISODateTimeFormat.date());

    public final static JacksonJodaDateFormat DEFAULT_TIMEONLY_FORMAT
        = createUTC(ISODateTimeFormat.time());

    /* 28-Jul-2015, tatu: As per [datatype-joda#70], there is difference between
     *    "dateTime()" and "dateTimeParser()"... so we need to differentiate between
     *    parser/generator it seems.
     */
    public final static JacksonJodaDateFormat DEFAULT_DATETIME_PARSER
        = createUTC(ISODateTimeFormat.dateTimeParser());

    public final static JacksonJodaDateFormat DEFAULT_DATETIME_PRINTER
        = createUTC(ISODateTimeFormat.dateTime());

    /**
     * @deprecated Since 2.6.1
     */
    @Deprecated
    public final static JacksonJodaDateFormat DEFAULT_DATETIME_FORMAT = DEFAULT_DATETIME_PRINTER;
    
    // should these differ from ones above? Presumably should use local timezone or... ?

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATEONLY_FORMAT
        = createDefaultTZ(ISODateTimeFormat.date());

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PRINTER
        = createDefaultTZ(ISODateTimeFormat.time());

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PARSER
        = createDefaultTZ(ISODateTimeFormat.localTimeParser());
    
    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PRINTER
        = createDefaultTZ(ISODateTimeFormat.dateTime());

    public final static JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PARSER
        = createDefaultTZ(ISODateTimeFormat.localDateOptionalTimeParser());

    public final static JacksonJodaPeriodFormat DEFAULT_PERIOD_FORMAT
        = new JacksonJodaPeriodFormat(ISOPeriodFormat.standard());

    public final static JacksonJodaDateFormat DEFAULT_YEAR_MONTH_FORMAT
            = new JacksonJodaDateFormat(ISODateTimeFormat.yearMonth());

    public final static JacksonJodaDateFormat DEFAULT_MONTH_DAY_FORMAT
            = createMonthDayFormat();

    // // // And then some wrapper methods for improved diagnostics, and possible
    // // // default settings for things like "withOffsetParsed()" (see
    // // // [dataformat-joda#75] for more information)
    
    private final static JacksonJodaDateFormat createUTC(DateTimeFormatter f) {
        return new JacksonJodaDateFormat(f.withZoneUTC());
    }

    private final static JacksonJodaDateFormat createDefaultTZ(DateTimeFormatter f) {
        return new JacksonJodaDateFormat(f.withZone(DEFAULT_TZ));
    }

    private final static JacksonJodaDateFormat createMonthDayFormat()
    {
        return new JacksonJodaDateFormat(ISODateTimeFormat.forFields(
                Arrays.asList(DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
                true, true));
    }
}
