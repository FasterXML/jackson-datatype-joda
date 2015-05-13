package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    private static final long serialVersionUID = 1L;

    // NOTE: formatter not used for printing at all, hence choice doesn't matter
    public InstantSerializer() { this(FormatConfig.DEFAULT_TIMEONLY_FORMAT); }
    public InstantSerializer(JacksonJodaDateFormat format) {
        super(Instant.class, format, false,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new InstantSerializer(formatter);
    }

    // @since 2.5
    @Override
    public boolean isEmpty(SerializerProvider prov, Instant value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        if (_useTimestamp(provider)) {
            gen.writeNumber(value.getMillis());
        } else {
            gen.writeString(value.toString());
        }
    }
}
