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
public final class DurationSerializer
    extends JodaDateSerializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    // NOTE: formatter is not really used directly for printing, but we do need
    // it as container for numeric/textual distinction
    
    public DurationSerializer() { this(FormatConfig.DEFAULT_DATEONLY_FORMAT); }
    public DurationSerializer(JacksonJodaDateFormat formatter) {
        // false -> no arrays (numbers)
        super(Duration.class, formatter, false,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Override
    public DurationSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new DurationSerializer(formatter);
    }

    // @since 2.5
    @Override
    public boolean isEmpty(SerializerProvider prov, Duration value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        if (_useTimestamp(provider)) {
            gen.writeNumber(value.getMillis());
        } else {
            gen.writeString(value.toString());
        }
    }
}
