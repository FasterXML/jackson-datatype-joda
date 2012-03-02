package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * Basic deserializer for {@link DateTime}. Accepts JSON String and Number
 * values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 *<p>
 * Since 1.6 this has been generic, to handle multiple related types,
 * including super types of {@link DateTime}
 */
public class DateTimeDeserializer<T extends ReadableInstant>
    extends JodaDeserializerBase<T>
{
    public DateTimeDeserializer(Class<T> cls) { super(cls); }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return (T) new DateTime(jp.getLongValue(), DateTimeZone.UTC);
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return (T) new DateTime(str, DateTimeZone.UTC);
        }
        throw ctxt.mappingException(getValueClass());
    }
}