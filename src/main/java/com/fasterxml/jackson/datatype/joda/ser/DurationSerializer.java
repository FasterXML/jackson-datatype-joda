package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
 */
public class DurationSerializer // non final since 2.6.1
    extends JodaDateSerializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    // NOTE: formatter is not really used directly for printing, but we do need
    // it as container for numeric/textual distinction
    
    public DurationSerializer() { this(FormatConfig.DEFAULT_DATEONLY_FORMAT, 0); }

    public DurationSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    
    public DurationSerializer(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        // false -> no arrays (numbers)
        super(Duration.class, formatter,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                FORMAT_TIMESTAMP, shapeOverride);
    }

    @Override
    public DurationSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new DurationSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Duration value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}
