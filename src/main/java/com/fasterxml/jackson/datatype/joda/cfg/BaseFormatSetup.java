package com.fasterxml.jackson.datatype.joda.cfg;

import java.util.Locale;

/**
 *  Class that holds the state of {@link JacksonJodaFormatBase} object.
 *  @see JacksonJodaFormatBase#JacksonJodaFormatBase(BaseFormatSetup)
 *  @see JacksonJodaFormatBase#getSetup()
 *  @since 2.8
 */
public class BaseFormatSetup {

    private Boolean _useTimestamp;

    private Locale _locale;

    public BaseFormatSetup(){

    }

    public BaseFormatSetup(BaseFormatSetup setup){
        this._useTimestamp = setup.isUseTimestamp();
        this._locale = setup._locale;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        this._locale = locale;
    }

    public Boolean isUseTimestamp() {
        return _useTimestamp;
    }

    public void setUseTimeStamp(Boolean useTimestamp) {
        this._useTimestamp = useTimestamp;
    }
}
