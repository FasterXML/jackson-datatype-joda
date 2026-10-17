package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.io.NumberInput;

import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * Deserializer for Joda {@link DateTimeZone}.
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    // [datatype-joda#197]
    private final static String INT_RANGE_MSG = "Offset value out of range of `int`";

    private static final long serialVersionUID = 1L;

    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            // for fun let's allow use of offsets...
            // [datatype-joda#197]: out-of-`int`-range value must be reported, not
            //   escape as `InputCoercionException` (but in-range `long` is fine)
            final int offset;
            try {
                offset = p.getIntValue();
            } catch (InputCoercionException e) {
                return (DateTimeZone) ctxt.handleWeirdNumberValue(handledType(),
                        p.getNumberValue(), INT_RANGE_MSG);
            }
            return _fromOffset(ctxt, offset);
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
        default:
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
            return _fromEmptyString(p, ctxt, value);
        }
        // 30-Sep-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isIntNumber(value)) {
            // [datatype-joda#197]: out-of-`int`-range value must be reported, not
            //   escape as a raw `NumberFormatException`
            final int offset;
            try {
                offset = NumberInput.parseInt(value);
            } catch (NumberFormatException e) {
                return (DateTimeZone) ctxt.handleWeirdStringValue(handledType(), value,
                        INT_RANGE_MSG);
            }
            return _fromOffset(ctxt, offset);
        }
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return DateTimeZone.forID(value);
        } catch (IllegalArgumentException e) {
            return (DateTimeZone) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        }
    }

    // @since 2.12
    protected DateTimeZone _fromOffset(DeserializationContext ctxt, int offset)
        throws IOException
    {
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return DateTimeZone.forOffsetHours(offset);
        } catch (IllegalArgumentException e) {
            return (DateTimeZone) ctxt.handleWeirdNumberValue(handledType(), offset,
                    "%s", e.getMessage());
        }
    }
}
