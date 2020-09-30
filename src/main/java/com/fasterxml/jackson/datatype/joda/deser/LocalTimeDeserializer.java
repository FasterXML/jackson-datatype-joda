package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalTimeDeserializer
    extends JodaDateDeserializerBase<LocalTime>
{
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
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        default:
        }
        // [HH,MM,ss,ms?]
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalTime) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON Array, String or Number");
    }

    // @since 2.12
    public LocalTime _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return (LocalTime) getNullValue(ctxt);
        }
        // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return new LocalTime(NumberInput.parseLong(value));
        }
        return _format.createParser(ctxt).parseLocalTime(value);
    }

    // @since 2.12
    public LocalTime _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws IOException
    {
        p.nextToken(); // VALUE_NUMBER_INT 
        int hour = p.getIntValue(); 
        p.nextToken(); // VALUE_NUMBER_INT
        int minute = p.getIntValue();
        p.nextToken(); // VALUE_NUMBER_INT
        int second = p.getIntValue();
        p.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
        int millis = 0;
        if (p.currentToken() != JsonToken.END_ARRAY) {
            millis = p.getIntValue();
            p.nextToken(); // END_ARRAY?
        }
        if (p.currentToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY, "after LocalTime ints");
        }
        return new LocalTime(hour, minute, second, millis);
    }
}
