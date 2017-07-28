package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class InstantSerializer // non final since 2.6.1
    extends JodaDateSerializerBase<Instant>
{
    private static final long serialVersionUID = 1L;

    // NOTE: formatter not used for printing at all, hence choice doesn't matter
    public InstantSerializer() { this(FormatConfig.DEFAULT_TIMEONLY_FORMAT, 0); }
    public InstantSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(Instant.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_TIMESTAMP, shapeOverride);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new InstantSerializer(formatter, shapeOverride);
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
        if (_serializationShape(provider) == FORMAT_STRING) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}
