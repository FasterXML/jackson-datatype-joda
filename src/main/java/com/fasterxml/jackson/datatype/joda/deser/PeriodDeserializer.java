package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Period;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;

public class PeriodDeserializer
    extends JodaDeserializerBase<Period>
{
    private static final long serialVersionUID = 1L;

    public PeriodDeserializer() { super(Period.class); }
   
    @Override
    public Period deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        // TODO: perhaps support array of numbers...
        //if (jp.isExpectedStartArrayToken()) { ]
        switch (jp.getCurrentToken()) {
        case VALUE_NUMBER_INT: // assume it's millisecond count
            return new Period(jp.getLongValue());            
        case VALUE_STRING:
            return new Period(jp.getText());
        default:
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Number or String");
   }
}
