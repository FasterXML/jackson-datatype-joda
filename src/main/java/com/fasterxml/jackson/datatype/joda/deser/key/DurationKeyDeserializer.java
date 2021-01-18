package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;

public class DurationKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected Object deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key).toStandardDuration();
        } catch (IOException e) {
            throw _wrapJodaFailure(e);
        }
    }
}
