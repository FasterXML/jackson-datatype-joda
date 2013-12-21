package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
 */
public final class DurationSerializer extends JodaSerializerBase<Duration>
{
    public DurationSerializer() { super(Duration.class); }

    @Override
    public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException
    {
        if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(value.toString());
        }
    }
}
