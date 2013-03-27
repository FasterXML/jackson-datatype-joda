package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.Interval;

import java.io.IOException;

/**
 * @author jkolobok
 */
public class IntervalDeserializer  extends JodaDeserializerBase<Interval>
{
    private static final long serialVersionUID = 5196071166239332742L;

    public IntervalDeserializer() {
        super(Interval.class);
    }

    @Override
    public Interval deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
    {
        if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
            String v = jsonParser.getText();
            int dashIndex = v.indexOf('-');
            long start = Long.valueOf(v.substring(0, dashIndex));
            long end = Long.valueOf(v.substring(dashIndex + 1));
            return new Interval(start, end);
        }
        throw deserializationContext.mappingException("expected JSON String");
    }
}
