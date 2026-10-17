package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

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
    private static final long serialVersionUID = 1L;

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
            return _fromTimestamp(ctxt, p.getLongValue());
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
        // [HH,MM,ss,ms?]
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalTime) ctxt.handleUnexpectedToken(handledType(), p.currentToken(), p,
                "expected JSON Array, String or Number");
    }

    // @since 2.12
    protected LocalTime _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
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
        throws IOException
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
        return new LocalTime(ts, _timestampZone(ctxt));
    }
}
