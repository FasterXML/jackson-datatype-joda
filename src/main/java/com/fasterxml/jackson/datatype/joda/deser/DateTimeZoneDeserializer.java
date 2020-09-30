package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;

import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * Deserializer for Joda {@link DateTimeZone}.
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            // for fun let's allow use of offsets...
            return _fromOffset(ctxt, p.getIntValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    // @since 2.12
    protected DateTimeZone _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return (DateTimeZone) getNullValue(ctxt);
        }
        // 30-Sep-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromOffset(ctxt, NumberInput.parseInt(value));
        }
        return DateTimeZone.forID(value);
    }

    // @since 2.12
    protected DateTimeZone _fromOffset(DeserializationContext ctxt, int offset) {
        return DateTimeZone.forOffsetHours(offset);
    }
}
