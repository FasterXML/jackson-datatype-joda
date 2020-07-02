package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalTimeDeserializer
    extends JodaDateDeserializerBase<LocalTime>
{
    private static final long serialVersionUID = 1L;

    public LocalTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PARSER);
    }

    public LocalTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalTimeDeserializer(format);
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return new LocalTime(p.getLongValue());            
        case JsonTokenId.ID_STRING:
            String str = p.getText().trim();
            return (str.length() == 0) ? null
                    : _format.createParser(ctxt).parseLocalTime(str);
        default:
        }
        // [HH,MM,ss,ms?]
        if (p.isExpectedStartArrayToken()) {
            p.nextToken(); // VALUE_NUMBER_INT 
            int hour = p.getIntValue(); 
            p.nextToken(); // VALUE_NUMBER_INT
            int minute = p.getIntValue();
            p.nextToken(); // VALUE_NUMBER_INT
            int second = p.getIntValue();
            p.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
            int millis = 0;
            if (p.getCurrentToken() != JsonToken.END_ARRAY) {
                millis = p.getIntValue();
                p.nextToken(); // END_ARRAY?
            }
            if (p.getCurrentToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY, "after LocalTime ints");
            }
            return new LocalTime(hour, minute, second, millis);
        }
        return (LocalTime) ctxt.handleUnexpectedToken(handledType(), p.getCurrentToken(), p,
                "expected JSON Array, String or Number");
    }
}