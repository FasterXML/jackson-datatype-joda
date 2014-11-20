package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    // NOTE: formatter not used for printing at all, hence choice doesn't matter
    public InstantSerializer() { this(FormatConfig.DEFAULT_TIMEONLY_FORMAT); }
    public InstantSerializer(JacksonJodaDateFormat format) {
        super(Instant.class, format, false,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new InstantSerializer(formatter);
    }

    // @since 2.5
    @Override
    public boolean isEmpty(Instant value) {
        return (value.getMillis() == 0L);
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
