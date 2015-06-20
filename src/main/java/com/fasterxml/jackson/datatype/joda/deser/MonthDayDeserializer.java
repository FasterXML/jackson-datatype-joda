package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.MonthDay;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * A Jackson deserializer for Joda MonthDay objects.
 * <p>
 * Expects a string value compatible with MonthDay's parse operation.
 */
public class MonthDayDeserializer extends JodaDeserializerBase<MonthDay>
{

    private static final long serialVersionUID = 1L;

    public MonthDayDeserializer()
    {
        super(MonthDay.class);
    }

    @Override
    public MonthDay deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING)
        {
            String str = jp.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
            return MonthDay.parse(str);
        }
        throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "expected JSON String");
    }
}
