package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalTimeSerializer
    extends JodaDateSerializerBase<LocalTime>
{
    protected final static JacksonJodaDateFormat DEFAULT_FORMAT
        = new JacksonJodaDateFormat(DEFAULT_TIMEONLY_FORMAT);

    public LocalTimeSerializer() { this(DEFAULT_FORMAT); }
    public LocalTimeSerializer(JacksonJodaDateFormat format) {
        super(LocalTime.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new LocalTimeSerializer(formatter);
    }

    @Override
    public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(value.hourOfDay().get());
            jgen.writeNumber(value.minuteOfHour().get());
            jgen.writeNumber(value.secondOfMinute().get());
            jgen.writeNumber(value.millisOfSecond().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }
}