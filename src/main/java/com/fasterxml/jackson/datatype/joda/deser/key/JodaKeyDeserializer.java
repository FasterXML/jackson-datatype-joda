package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

abstract class JodaKeyDeserializer extends KeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (key.length() == 0) { // [JACKSON-360]
            return null;
        }
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException;
}