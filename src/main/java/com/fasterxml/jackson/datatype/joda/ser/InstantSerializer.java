package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    protected final static JacksonJodaDateFormat DEFAULT_FORMAT = new JacksonJodaDateFormat(DEFAULT_DATEONLY_FORMAT);
    
    public InstantSerializer() { this(DEFAULT_FORMAT); }
    public InstantSerializer(JacksonJodaDateFormat format) {
        super(Instant.class, format, false,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaDateFormat formatter) {
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
}
