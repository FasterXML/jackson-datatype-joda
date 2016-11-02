package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.Duration;

import java.io.IOException;

public class DurationKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    @Override
    protected Duration deserialize(String key, DeserializationContext ctxt) throws IOException {
        return Duration.parse(key);
    }
}
