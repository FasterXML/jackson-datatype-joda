package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JodaMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;

    public JodaMapper() {
        registerModule(new JodaModule());
    }

    /**
     * Convenience method that is shortcut for:
     *<pre>
     *  module.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
     *</pre>
     */
    public boolean getWriteDatesAsTimestamps() {
        return getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Convenience method that is shortcut for:
     *<pre>
     *  configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, state)
     *</pre>
     */
    public void setWriteDatesAsTimestamps(boolean state) {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, state);
    }
}
