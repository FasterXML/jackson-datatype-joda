package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Serializes a Duration as its number of millis.
 */
public final class DurationSerializer extends StdScalarSerializer<Duration>
{

    public DurationSerializer() { super(Duration.class); }

    @Override
    public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException
    {
        jgen.writeNumber(value.getMillis());
    }
}
