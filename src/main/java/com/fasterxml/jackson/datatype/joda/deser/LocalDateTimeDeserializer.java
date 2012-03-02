package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateTimeDeserializer
    extends JodaDeserializerBase<LocalDateTime>
{
    public LocalDateTimeDeserializer() { super(LocalDateTime.class); }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // We'll accept either long (timestamp) or array:
        if (jp.isExpectedStartArrayToken()) {
            jp.nextToken(); // VALUE_NUMBER_INT
            int year = jp.getIntValue();
            jp.nextToken(); // VALUE_NUMBER_INT
            int month = jp.getIntValue();
            jp.nextToken(); // VALUE_NUMBER_INT
            int day = jp.getIntValue();
            jp.nextToken(); // VALUE_NUMBER_INT
            int hour = jp.getIntValue();
            jp.nextToken(); // VALUE_NUMBER_INT
            int minute = jp.getIntValue();
            jp.nextToken(); // VALUE_NUMBER_INT
            int second = jp.getIntValue();
            // let's leave milliseconds optional?
            int millisecond = 0;
            if (jp.nextToken() != JsonToken.END_ARRAY) { // VALUE_NUMBER_INT           
                millisecond = jp.getIntValue();
                jp.nextToken(); // END_ARRAY?
            }
            if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "after LocalDateTime ints");
            }
            return new LocalDateTime(year, month, day, hour, minute, second, millisecond);                 
        }

        switch (jp.getCurrentToken()) {
        case VALUE_NUMBER_INT:
            return new LocalDateTime(jp.getLongValue());            
        case VALUE_STRING:
            DateTime local = parseLocal(jp);
            if (local == null) {
                return null;
            }
            return local.toLocalDateTime();
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Array or Number");
    }
}