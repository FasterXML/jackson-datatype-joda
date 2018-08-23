package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalTimeSerializer // non final since 2.6.1
    extends JodaDateSerializerBase<LocalTime>
{
    private static final long serialVersionUID = 1L;

    public LocalTimeSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PRINTER, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(LocalTime.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_ARRAY, shapeOverride);
    }

    @Override
    public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new LocalTimeSerializer(formatter, shapeOverride);
    }

    // is there a natural "empty" value to check against?
    /*
    @Override
    public boolean isEmpty(LocalTime value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            gen.writeString(_format.createFormatter(provider).print(value));
            return;
        }
        // Timestamp here actually means an array of values
        gen.writeStartArray();
        gen.writeNumber(value.hourOfDay().get());
        gen.writeNumber(value.minuteOfHour().get());
        gen.writeNumber(value.secondOfMinute().get());
        gen.writeNumber(value.millisOfSecond().get());
        gen.writeEndArray();
    }
}
