package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class LocalDateSerializer
    extends JodaDateSerializerBase<LocalDate>
{
    public LocalDateSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT); }
    public LocalDateSerializer(JacksonJodaDateFormat format) {
        super(LocalDate.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public LocalDateSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new LocalDateSerializer(formatter);
    }

    // is there a natural "empty" value to check against?
 /*
    @Override
    public boolean isEmpty(LocalDate value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(value.year().get());
            jgen.writeNumber(value.monthOfYear().get());
            jgen.writeNumber(value.dayOfMonth().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }
}
