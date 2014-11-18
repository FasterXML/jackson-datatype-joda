package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;

import org.joda.time.format.ISODateTimeFormat;

public final class DateTimeSerializer
    extends JodaDateSerializerBase<DateTime>
{
    protected final static JacksonJodaDateFormat DEFAULT_FORMAT
        = new JacksonJodaDateFormat(ISODateTimeFormat.dateTime().withZoneUTC());
    
    public DateTimeSerializer() { this(DEFAULT_FORMAT); }
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
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }
}
