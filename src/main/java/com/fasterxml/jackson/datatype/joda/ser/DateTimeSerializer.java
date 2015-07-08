package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class DateTimeSerializer
    extends JodaDateSerializerBase<DateTime>
{
    private static final long serialVersionUID = 1L;

    public DateTimeSerializer() { this(FormatConfig.DEFAULT_DATETIME_FORMAT); }
    public DateTimeSerializer(JacksonJodaDateFormat format) {
        // false -> no arrays (numbers)
        super(DateTime.class, format, false,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public DateTimeSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new DateTimeSerializer(formatter);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, DateTime value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        // First: simple, non-timezone-included output
        if (!provider.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)) {
            if (_useTimestamp(provider)) {
                gen.writeNumber(value.getMillis());
            } else {
                gen.writeString(_format.createFormatter(provider).print(value));
            }
        } else {
            // and then as per [datatype-joda#44], optional TimeZone inclusion
            
            StringBuilder sb;
            
            if (_useTimestamp(provider)) {
                sb = new StringBuilder(20)
                    .append(value.getMillis());
            } else {
                sb = new StringBuilder(40)
                    .append(_format.createFormatter(provider).print(value));
            }
            sb = sb.append('[')
                    .append(value.getZone())
                    .append(']');
            gen.writeString(sb.toString());
        }
    }
}
