package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
 */
public final class DurationSerializer
    extends JodaDateSerializerBase<Duration>
{
    protected final static JacksonJodaFormat DEFAULT_FORMAT = new JacksonJodaFormat(DEFAULT_DATEONLY_FORMAT);

    public DurationSerializer() { this(DEFAULT_FORMAT); }
    public DurationSerializer(JacksonJodaFormat formatter) {
        super(Duration.class, formatter);
    }

    @Override
    public DurationSerializer withFormat(JacksonJodaFormat formatter) {
        return (_format == formatter) ? this : new DurationSerializer(formatter);
    }

    @Override
    public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException
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
