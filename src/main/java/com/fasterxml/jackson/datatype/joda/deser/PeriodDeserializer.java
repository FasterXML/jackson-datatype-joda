package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Period;
import org.joda.time.ReadablePeriod;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class PeriodDeserializer
    extends JodaDeserializerBase<ReadablePeriod>
{
    public PeriodDeserializer() { super(ReadablePeriod.class); }
   
    @Override
    public ReadablePeriod deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        // TODO: perhaps support array of numbers...
        //if (jp.isExpectedStartArrayToken()) { ]
        switch (jp.getCurrentToken()) {
        case VALUE_NUMBER_INT: // assume it's millisecond count
            return new Period(jp.getLongValue());            
        case VALUE_STRING:
            return new Period(jp.getText());
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Number or String");
   }
}
