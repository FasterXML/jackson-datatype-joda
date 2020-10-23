package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateDeserializer
    extends JodaDateDeserializerBase<LocalDate>
{
    private static final long serialVersionUID = 1L;

    public LocalDateDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT);
    }
    
    public LocalDateDeserializer(JacksonJodaDateFormat format) {
        super(LocalDate.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateDeserializer(format);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        default:
        }
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalDate) ctxt.handleUnexpectedToken(handledType(), p.currentToken(), p,
                "expected String, Number or JSON Array");
    }

    // @since 2.12
    protected LocalDate _fromString(final JsonParser p, final DeserializationContext ctxt,
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
        return _format.createParser(ctxt).parseLocalDate(value);
    }

    // @since 2.12
    protected LocalDate _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws IOException
    {
        // [yyyy,mm,dd] or ["yyyy","mm","dd"]
        int year = p.nextIntValue(-1); // fast speculative case
        if (year == -1) { // either -1, or not an integral number; slow path
            year = _parseIntPrimitive(p, ctxt);
        }
        int month = p.nextIntValue(-1);
        if (month == -1) {
            month = _parseIntPrimitive(p, ctxt);
        }
        int day = p.nextIntValue(-1);
        if (day == -1) {
            day = _parseIntPrimitive(p, ctxt);
        }
        if (p.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY, "after LocalDate ints");
        }
        return new LocalDate(year, month, day);
    }

    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        return new LocalDate(ts, tz);
    }
}
