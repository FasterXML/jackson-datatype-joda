package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JodaMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;

    public JodaMapper() {
        registerModule(new JodaModule());
    }

    public boolean getWriteDatesAsTimestamps() {
        return getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void setWriteDatesAsTimestamps(boolean writeDatesAsTimestamps) {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, writeDatesAsTimestamps);
    }
}
