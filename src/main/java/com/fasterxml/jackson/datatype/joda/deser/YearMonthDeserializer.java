package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.YearMonth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * A Jackson deserializer for Joda YearMonth objects.
 * <p>
 * Expects a string value compatible with YearMonth's parse operation.
 */
public class YearMonthDeserializer extends JodaDeserializerBase<YearMonth>
{

    private static final long serialVersionUID = -3830851040664795250L;

    public YearMonthDeserializer()
    {
        super(YearMonth.class);
    }

    @Override
    public YearMonth deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING)
        {
            String str = jp.getText().trim();
            if (str.isEmpty())
            {
                return null;
            }
            return YearMonth.parse(str);
        }
        throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "expected JSON String");
    }

}
