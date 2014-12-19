package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class LocalTimeSerializer
    extends JodaDateSerializerBase<LocalTime>
{
    private static final long serialVersionUID = 1L;

    public LocalTimeSerializer() { this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_FORMAT); }
    public LocalTimeSerializer(JacksonJodaDateFormat format) {
        super(LocalTime.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new LocalTimeSerializer(formatter);
    }

    // is there a natural "empty" value to check against?
    /*
    @Override
    public boolean isEmpty(LocalTime value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException
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