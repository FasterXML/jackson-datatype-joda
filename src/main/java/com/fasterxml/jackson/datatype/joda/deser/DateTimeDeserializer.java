package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.*;

import java.io.IOException;
import java.util.TimeZone;

/**
 * Basic deserializer for {@link ReadableDateTime} and its subtypes.
 * Accepts JSON String and Number values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 */
public class DateTimeDeserializer
    extends JodaDateDeserializerBase<ReadableInstant>
{
    private static final long serialVersionUID = 1L;

    public DateTimeDeserializer(Class<?> cls, JacksonJodaDateFormat format) {
        super(cls, format);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls)
    {
        return (JsonDeserializer<T>) new DateTimeDeserializer(cls,
                FormatConfig.DEFAULT_DATETIME_FORMAT);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateTimeDeserializer(_valueClass, format);
    }

    @Override
    public ReadableDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_NUMBER_INT) {
            TimeZone tz = ctxt.getTimeZone();
            DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
            return new DateTime(p.getLongValue(), dtz);
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            // Not sure if it should use timezone or not...
            return _format.createParser(ctxt).parseDateTime(str);
        }
        throw ctxt.mappingException(handledType());
    }
}
