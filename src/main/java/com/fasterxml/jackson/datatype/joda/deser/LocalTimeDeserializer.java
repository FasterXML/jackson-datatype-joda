package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalTimeDeserializer
    extends JodaDeserializerBase<LocalTime>
{
    private static final long serialVersionUID = 1L;

    final static DateTimeFormatter parser = ISODateTimeFormat.localTimeParser();

    public LocalTimeDeserializer() { super(LocalTime.class); }

    @Override
    public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException
    {
        switch (jp.getCurrentToken()) {
        case START_ARRAY:
            // [HH,MM,ss,ms?]
            if (jp.isExpectedStartArrayToken()) {
                jp.nextToken(); // VALUE_NUMBER_INT 
                int hour = jp.getIntValue(); 
                jp.nextToken(); // VALUE_NUMBER_INT
                int minute = jp.getIntValue();
                jp.nextToken(); // VALUE_NUMBER_INT
                int second = jp.getIntValue();
                jp.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
                int millis = 0;
                if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                    millis = jp.getIntValue();
                    jp.nextToken(); // END_ARRAY?
                }
                if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "after LocalTime ints");
                }
                return new LocalTime(hour, minute, second, millis);
            }
            break;
        case VALUE_NUMBER_INT:
            return new LocalTime(jp.getLongValue());            
        case VALUE_STRING:
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return parser.parseLocalTime(str);
        default:
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Array, String or Number");
    }
}