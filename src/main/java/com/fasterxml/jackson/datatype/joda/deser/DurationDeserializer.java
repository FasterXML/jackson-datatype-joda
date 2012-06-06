package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Deserializes a Duration from either an int number of millis or using the {@link Duration#Duration(Object)}
 * constructor on a JSON string. By default the only supported string format is that used by {@link
 * Duration#toString()}. (That format for a 3,248 millisecond duration is "PT3.248S".)
 */
public final class DurationDeserializer extends StdScalarDeserializer<Duration>
{

    public DurationDeserializer() { super(Duration.class); }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws
        IOException, JsonProcessingException
    {
        switch (jsonParser.getCurrentToken()) {
        case VALUE_NUMBER_INT: // assume it's millisecond count
            return new Duration(jsonParser.getLongValue());
        case VALUE_STRING:
            return new Duration(jsonParser.getText());
        }
        throw deserializationContext.mappingException("expected JSON Number or String");
    }
}

