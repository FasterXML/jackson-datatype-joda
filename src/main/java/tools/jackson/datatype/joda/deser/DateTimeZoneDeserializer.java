package tools.jackson.datatype.joda.deser;

import org.joda.time.DateTimeZone;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.io.NumberInput;

import tools.jackson.databind.DeserializationContext;

/**
 * Deserializer for Joda {@link DateTimeZone}.
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    // [datatype-joda#197]: same message for all invalid offsets, whether
    //   out of `int` range or not
    private final static String INVALID_OFFSET_MSG = "Offset hours must be in range [-23, 23]";

    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            // for fun let's allow use of offsets...
            // [datatype-joda#197]: `long`/`BigInteger` must not fail on `getIntValue()`
            if (p.getNumberType() != JsonParser.NumberType.INT) {
                return (DateTimeZone) ctxt.handleWeirdNumberValue(handledType(),
                        p.getNumberValue(), INVALID_OFFSET_MSG);
            }
            return _fromOffset(ctxt, p.getIntValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getString());
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
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // 30-Sep-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isIntNumber(value)) {
            // [datatype-joda#197]: any invalid offset (out of `int` range or not)
            //   must be reported, not escape as a raw `NumberFormatException`
            //   or Joda `IllegalArgumentException`
            try {
                return DateTimeZone.forOffsetHours(NumberInput.parseInt(value));
            } catch (IllegalArgumentException e) { // includes NumberFormatException
                return (DateTimeZone) ctxt.handleWeirdStringValue(handledType(), value,
                        INVALID_OFFSET_MSG);
            }
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
    protected DateTimeZone _fromOffset(DeserializationContext ctxt, int offset) {
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return DateTimeZone.forOffsetHours(offset);
        } catch (IllegalArgumentException e) {
            return (DateTimeZone) ctxt.handleWeirdNumberValue(handledType(), offset,
                    INVALID_OFFSET_MSG);
        }
    }
}
