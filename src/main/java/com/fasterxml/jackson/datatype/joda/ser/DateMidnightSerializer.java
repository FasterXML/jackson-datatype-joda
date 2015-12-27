package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateMidnight;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Note: Joda has <code>DateMidnight</code> deprecated since at least 2.4,
 * but we still support it for now.
 *
 * @deprecated Since 2.7
 */
@Deprecated // since Jackson 2.7 (and Joda 2.4)
public class DateMidnightSerializer
    extends JodaDateSerializerBase<DateMidnight>
{
    private static final long serialVersionUID = 1L;

    public DateMidnightSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT); }
    public DateMidnightSerializer(JacksonJodaDateFormat format) {
        // true -> use arrays
        super(DateMidnight.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public DateMidnightSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new DateMidnightSerializer(formatter);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, DateMidnight value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(DateMidnight value, JsonGenerator gen,
            SerializerProvider provider) throws IOException
    {
        if (_useTimestamp(provider)) {
            // same as with other date-only values
            gen.writeStartArray();
            gen.writeNumber(value.year().get());
            gen.writeNumber(value.monthOfYear().get());
            gen.writeNumber(value.dayOfMonth().get());
            gen.writeEndArray();
        } else {
            gen.writeString(_format.createFormatterWithLocale(provider).print(value));
        }
    }
}
