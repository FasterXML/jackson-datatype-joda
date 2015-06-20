package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateTimeDeserializer
    extends JodaDateDeserializerBase<LocalDateTime>
{
    private static final long serialVersionUID = 1L;

    public LocalDateTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATETIME_PARSER);
    }
    
    public LocalDateTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalDateTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateTimeDeserializer(format);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        switch (p.getCurrentToken()) {
        case VALUE_STRING:
            String str = p.getText().trim();
            return (str.length() == 0) ? null
                    : _format.createParser(ctxt).parseLocalDateTime(str);
        case VALUE_NUMBER_INT:
            return new LocalDateTime(p.getLongValue());            
        case START_ARRAY:
            // [yyyy,mm,dd,hh,MM,ss,ms]
            if (p.isExpectedStartArrayToken()) {
                p.nextToken(); // VALUE_NUMBER_INT
                int year = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT
                int month = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT
                int day = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT
                int hour = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT
                int minute = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT
                int second = p.getIntValue();
                p.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
                // let's leave milliseconds optional?
                int millisecond = 0;
                if (p.getCurrentToken() != JsonToken.END_ARRAY) { // VALUE_NUMBER_INT           
                    millisecond = p.getIntValue();
                    p.nextToken(); // END_ARRAY?
                }
                if (p.getCurrentToken() != JsonToken.END_ARRAY) {
                    throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "after LocalDateTime ints");
                }
                return new LocalDateTime(year, month, day, hour, minute, second, millisecond);                 
            }
            break;
        default:
        }
        throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "expected JSON Array, Number or String");
    }
}