package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;


public class LocalDateTimeDeserializer
    extends JodaDeserializerBase<LocalDateTime>
{
    private static final long serialVersionUID = 1L;

    final static DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    public LocalDateTimeDeserializer() { super(LocalDateTime.class); }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        switch (jp.getCurrentToken()) {
        case START_ARRAY:
            // [yyyy,mm,dd,hh,MM,ss,ms]
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
                jp.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
                // let's leave milliseconds optional?
                int millisecond = 0;
                if (jp.getCurrentToken() != JsonToken.END_ARRAY) { // VALUE_NUMBER_INT           
                    millisecond = jp.getIntValue();
                    jp.nextToken(); // END_ARRAY?
                }
                if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "after LocalDateTime ints");
                }
                return new LocalDateTime(year, month, day, hour, minute, second, millisecond);                 
            }
            break;
        case VALUE_NUMBER_INT:
            return new LocalDateTime(jp.getLongValue());            
        case VALUE_STRING:
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return parser.parseLocalDateTime(str);
        default:
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Array, Number or String");
    }
}