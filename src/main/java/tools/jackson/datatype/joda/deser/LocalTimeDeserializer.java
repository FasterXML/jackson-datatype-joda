package tools.jackson.datatype.joda.deser;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

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
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
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
        // [HH,MM,ss,ms?]
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalTime) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON Array, String or Number");
    }

    // @since 2.12
    protected LocalTime _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }
        final DateTimeFormatter parser = _format.createParser(ctxt);
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return parser.parseLocalTime(value);
        } catch (IllegalArgumentException e) {
            return (LocalTime) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        }
    }

    // @since 2.12
    protected LocalTime _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        // [HH,MM,ss,ms?] (or Strings)
        final String msg = "expected 3 or 4 int values for LocalTime";
        int hour = _nextArrayInt(p, ctxt, msg);
        int minute = _nextArrayInt(p, ctxt, msg);
        int second = _nextArrayInt(p, ctxt, msg);
        // milliseconds are optional
        int millis = 0;
        if (p.nextToken() != JsonToken.END_ARRAY) {
            millis = _parseIntPrimitive(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.END_ARRAY,
                        "after LocalTime ints");
            }
        }
        try {
            return new LocalTime(hour, minute, second, millis);
        } catch (IllegalFieldValueException e) {
            throw _invalidArrayValue(ctxt, e);
        }
    }

    protected LocalTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        // 14-Oct-2026: zone offset addition for edge-of-long timestamp can
        //   overflow; do not let raw Joda exception escape
        try {
            return new LocalTime(ts, _timestampZone(ctxt));
        } catch (ArithmeticException e) {
            return (LocalTime) ctxt.handleWeirdNumberValue(handledType(), ts,
                    "%s", e.getMessage());
        }
    }
}
