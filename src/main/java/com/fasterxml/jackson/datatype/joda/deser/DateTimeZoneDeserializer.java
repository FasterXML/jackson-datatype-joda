package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * Deserializes Joda {@link DateTimeZone}.
 * Until https://jira.codehaus.org/browse/JACKSON-909 is fixed, here's my take.
 * @author ceefour
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    private static final long serialVersionUID = 1L;

    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            // for fun let's allow use of offsets...
            return DateTimeZone.forOffsetHours(jp.getIntValue());
        }
        if (t == JsonToken.VALUE_STRING) {
            return DateTimeZone.forID(jp.getText().trim());
        }
        throw ctxt.mappingException(DateTimeZone.class, JsonToken.VALUE_STRING);
    }
}
