package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    public InstantSerializer() { this(null); }
    public InstantSerializer(Boolean useTimestamp) {
        super(Instant.class, useTimestamp);
    }

    @Override
    public InstantSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return (useTimestamp == _useTimestamp) ? this
                : new InstantSerializer(useTimestamp);
    }

    @Override
    public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(value.toString());
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "number" : "string", true);
    }
}
