package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class DateMidnightDeserializer
    extends JodaDeserializerBase<DateMidnight>
{
    final static DateTimeFormatter parser = ISODateTimeFormat.localDateParser();

    public DateMidnightDeserializer() { super(DateMidnight.class); }

    @Override
    public DateMidnight deserialize(JsonParser jp, DeserializationContext ctxt)
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
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "after DateMidnight ints");
            }
            return new DateMidnight(year, month, day);
        }
        switch (jp.getCurrentToken()) {
        case VALUE_NUMBER_INT:
            return new DateMidnight(jp.getLongValue());            
        case VALUE_STRING:
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            LocalDate local = parser.parseLocalDate(str);
            if (local == null) {
                return null;
            }
            return local.toDateMidnight();
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Array, Number or String");
    }
}