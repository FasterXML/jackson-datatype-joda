package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.IllegalFieldValueException;
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
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
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
        final String msg = "expected 3 int values for LocalDate";
        int year = _nextArrayInt(p, ctxt, msg);
        int month = _nextArrayInt(p, ctxt, msg);
        int day = _nextArrayInt(p, ctxt, msg);
        if (p.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.END_ARRAY, "after LocalDate ints");
        }
        try {
            return new LocalDate(year, month, day);
        } catch (IllegalFieldValueException e) {
            throw _invalidArrayValue(ctxt, e);
        }
    }

    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new LocalDate(ts, _timestampZone(ctxt));
    }
}
