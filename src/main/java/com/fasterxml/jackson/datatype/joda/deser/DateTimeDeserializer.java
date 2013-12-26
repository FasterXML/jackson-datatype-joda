package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import java.io.IOException;
import java.util.TimeZone;

/**
 * Basic deserializer for {@link ReadableDateTime} and its subtypes.
 * Accepts JSON String and Number values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 */
public class DateTimeDeserializer
    extends JodaDeserializerBase<ReadableInstant>
{
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public DateTimeDeserializer(Class<? extends ReadableInstant> cls) {
        super((Class<ReadableInstant>)cls);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls)
    {
        return (JsonDeserializer<T>) new DateTimeDeserializer(cls);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ReadableDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        TimeZone tz = ctxt.getTimeZone();
        DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);

        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new DateTime(jp.getLongValue(), dtz);
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return new DateTime(str, dtz);
        }
        // TODO: in 2.4, use 'handledType()'
        throw ctxt.mappingException(getValueClass());
    }
}
