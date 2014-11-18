package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.joda.time.Interval;

import java.io.IOException;

public class IntervalDeserializer extends JodaDeserializerBase<Interval>
{
    private static final long serialVersionUID = 5196071166239332742L;

    public IntervalDeserializer() {
        super(Interval.class);
    }

    @Override
    public Interval deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
    {
        JsonToken t = jsonParser.getCurrentToken();
        if (t != JsonToken.VALUE_STRING) {
            throw deserializationContext.mappingException("expected JSON String, got "+t);
        }
        String v = jsonParser.getText().trim();

        /* 17-Nov-2014, tatu: Actually let's start with slash, instead of hyphen, because
         *   that is the separator for standard functionality...
         */
        int index = v.indexOf('/', 1);
        if (index < 0) {
            index = v.indexOf('-', 1);
        }
        if (index < 0) {
            throw deserializationContext.weirdStringException(v, handledType(), "no slash or hyphen found to separate start, end");
        }
        long start, end;
        String str = v.substring(0, index);
        try {
            start = Long.valueOf(str);
            str = v.substring(index + 1);
            end = Long.valueOf(str);
        } catch (NumberFormatException e) {
            throw JsonMappingException.from(jsonParser,
                    "Failed to parse number from '"+str+"' (full source String '"+v+"') to construct "+handledType().getName());
        }
        return new Interval(start, end);
    }
}
