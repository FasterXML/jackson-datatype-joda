package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

public class PeriodKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected Object deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key);
        } catch (IOException e) {
            throw _wrapJodaFailure(e);
        }
    }
}