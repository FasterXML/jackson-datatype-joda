package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    protected final static JacksonJodaFormat DEFAULT_FORMAT = new JacksonJodaFormat(DEFAULT_DATEONLY_FORMAT);
    
    public InstantSerializer() { this(DEFAULT_FORMAT); }
    public InstantSerializer(JacksonJodaFormat format) {
        super(Instant.class, format);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaFormat formatter) {
        return (_format == formatter) ? this : new InstantSerializer(formatter);
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
