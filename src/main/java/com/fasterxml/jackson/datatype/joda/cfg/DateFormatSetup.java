package com.fasterxml.jackson.datatype.joda.cfg;

import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

/**
 *  Class that holds the state of {@link JacksonJodaDateFormat} object.
 *  @see JacksonJodaDateFormat#JacksonJodaDateFormat(DateFormatSetup)
 *  @see JacksonJodaDateFormat#getSetup()
 *  @since 2.8
 */
public class DateFormatSetup extends BaseFormatSetup {

    private DateTimeFormatter _formatter;

    private TimeZone _timeZone;

    private Boolean adjustToContextTZOverride;

    public DateFormatSetup() {

    }

    public DateFormatSetup(BaseFormatSetup format) {
        super(format);
    }

    public DateTimeFormatter getFormatter() {
        return _formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this._formatter = formatter;
    }

    public TimeZone getTimeZone() {
        return _timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this._timeZone = timeZone;
    }

    public Boolean getAdjustToContextTZOverride() {
        return adjustToContextTZOverride;
    }

    public void setAdjustToContextTZOverride(Boolean adjustToContextTZOverride) {
        this.adjustToContextTZOverride = adjustToContextTZOverride;
    }
}
