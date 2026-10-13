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
    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            // for fun let's allow use of offsets...
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
                && _isValidTimestampString(value)) {
            // 13-Oct-2026: [datatype-joda#197] value only validated for `long` range,
            //   but an offset is `int`; out-of-range must be reported, not escape as a
            //   raw `NumberFormatException` from `parseInt`
            long offset = NumberInput.parseLong(value);
            if (offset < Integer.MIN_VALUE || offset > Integer.MAX_VALUE) {
                return (DateTimeZone) ctxt.handleWeirdStringValue(handledType(), value,
                        "Timestamp value out of range of `int`");
            }
            return _fromOffset(ctxt, (int) offset);
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
                    "%s", e.getMessage());
        }
    }
}
