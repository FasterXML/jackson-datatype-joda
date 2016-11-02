package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * Deserializer for Joda {@link DateTimeZone}.
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    private static final long serialVersionUID = 1L;

    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            // for fun let's allow use of offsets...
            return DateTimeZone.forOffsetHours(p.getIntValue());
        }
        if (t == JsonToken.VALUE_STRING) {
            return DateTimeZone.forID(p.getText().trim());
        }
        return _handleNotNumberOrString(p, ctxt);
    }
}
