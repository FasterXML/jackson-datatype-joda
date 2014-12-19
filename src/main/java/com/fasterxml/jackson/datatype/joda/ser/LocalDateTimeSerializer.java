package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class LocalDateTimeSerializer
    extends JodaDateSerializerBase<LocalDateTime>
{
    private static final long serialVersionUID = 1L;

    public LocalDateTimeSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATETIME_FORMAT); }
    public LocalDateTimeSerializer(JacksonJodaDateFormat format) {
        super(LocalDateTime.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public LocalDateTimeSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new LocalDateTimeSerializer(formatter);
    }

    // is there a natural "empty" value to check against?
 /*
    @Override
    public boolean isEmpty(LocalDateTime value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(value.year().get());
            jgen.writeNumber(value.monthOfYear().get());
            jgen.writeNumber(value.dayOfMonth().get());
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