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

    public DateMidnightSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT, 0);
    }

    public DateMidnightSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public DateMidnightSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        // true -> use arrays
        super(DateMidnight.class, format,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_ARRAY, shapeOverride);
    }

    @Override
    public DateMidnightSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new DateMidnightSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, DateMidnight value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(DateMidnight value, JsonGenerator gen,
            SerializerProvider provider) throws IOException
    {
        switch (_serializationShape(provider)) {
        case FORMAT_STRING:
            gen.writeString(_format.createFormatterWithLocale(provider).print(value));
            break;
        case FORMAT_TIMESTAMP:
            gen.writeNumber(value.getMillis());
            break;
        case FORMAT_ARRAY:
            // same as with other date-only values
            gen.writeStartArray();
            gen.writeNumber(value.year().get());
            gen.writeNumber(value.monthOfYear().get());
            gen.writeNumber(value.dayOfMonth().get());
            gen.writeEndArray();
        }
    }
}
