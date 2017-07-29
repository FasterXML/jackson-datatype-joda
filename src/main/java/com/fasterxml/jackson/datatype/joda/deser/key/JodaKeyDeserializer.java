package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

import java.io.IOException;

abstract class JodaKeyDeserializer extends KeyDeserializer
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    protected final static JacksonJodaPeriodFormat PERIOD_FORMAT = FormatConfig.DEFAULT_PERIOD_FORMAT;

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key.length() == 0) { // [JACKSON-360]
            return null;
        }
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt) throws IOException;
}