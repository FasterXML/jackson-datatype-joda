package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateSerializer
    extends JodaDateSerializerBase<LocalDate>
{
    public LocalDateSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT, 0); }
    public LocalDateSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public LocalDateSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(LocalDate.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_ARRAY, shapeOverride);
    }



    @Override
    public LocalDateSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new LocalDateSerializer(formatter, shapeOverride);
    }

    // is there a natural "empty" value to check against?
 /*
    @Override
    public boolean isEmpty(LocalDate value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            gen.writeString(_format.createFormatter(provider).print(value));
            return;
        }
        // 28-Jul-2017, tatu: Wrt [dataformat-joda#39]... we could perhaps support timestamps,
        //   but only by specifying what to do with time (`LocalTime`) AND timezone. For now,
        //   seems like asking for trouble really... so only use array notation.
        gen.writeStartArray();
        gen.writeNumber(value.year().get());
        gen.writeNumber(value.monthOfYear().get());
        gen.writeNumber(value.dayOfMonth().get());
        gen.writeEndArray();
    }
}
