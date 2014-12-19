package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateMidnight;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public final class DateMidnightSerializer
    extends JodaDateSerializerBase<DateMidnight>
{
    private static final long serialVersionUID = 1L;

    public DateMidnightSerializer() { this(FormatConfig.DEFAULT_DATEONLY_FORMAT); }
    public DateMidnightSerializer(JacksonJodaDateFormat format) {
        // true -> use arrays
        super(DateMidnight.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public DateMidnightSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new DateMidnightSerializer(_format);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, DateMidnight value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(DateMidnight value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // same as with other date-only values
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