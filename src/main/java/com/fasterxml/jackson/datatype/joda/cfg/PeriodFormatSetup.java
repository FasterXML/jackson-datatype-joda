package com.fasterxml.jackson.datatype.joda.cfg;

import org.joda.time.format.PeriodFormatter;

/**
 *  Class that holds the state of {@link JacksonJodaPeriodFormat} object.
 *  @see JacksonJodaPeriodFormat#JacksonJodaPeriodFormat(PeriodFormatSetup)
 *  @see JacksonJodaPeriodFormat#getSetup()
 *  @since 2.8
 */
public class PeriodFormatSetup extends BaseFormatSetup {

    private PeriodFormatter _formatter;

    public PeriodFormatSetup() {

    }

    public PeriodFormatSetup(BaseFormatSetup setup) {
        super(setup);
    }

    public PeriodFormatter getFormatter() {
        return _formatter;
    }

    public void setFormatter(PeriodFormatter formatter) {
        this._formatter = formatter;
    }
}
