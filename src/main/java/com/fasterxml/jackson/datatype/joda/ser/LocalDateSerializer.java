package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.LocalDate;

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
    public void serialize(LocalDate value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            g.writeString(_format.createFormatter(provider).print(value));
            return;
        }
        // 28-Jul-2017, tatu: Wrt [dataformat-joda#39]... we could perhaps support timestamps,
        //   but only by specifying what to do with time (`LocalTime`) AND timezone. For now,
        //   seems like asking for trouble really... so only use array notation.
        g.writeStartArray();
        g.writeNumber(value.year().get());
        g.writeNumber(value.monthOfYear().get());
        g.writeNumber(value.dayOfMonth().get());
        g.writeEndArray();
    }
}
