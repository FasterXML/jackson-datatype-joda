package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
 */
public final class DurationSerializer
    extends JodaDateSerializerBase<Duration>
{
    protected final static JacksonJodaDateFormat DEFAULT_FORMAT = new JacksonJodaDateFormat(DEFAULT_DATEONLY_FORMAT);

    public DurationSerializer() { this(DEFAULT_FORMAT); }
    public DurationSerializer(JacksonJodaDateFormat formatter) {
        // false -> no arrays (numbers)
        super(Duration.class, formatter, false,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Override
    public DurationSerializer withFormat(JacksonJodaDateFormat formatter) {
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
}
