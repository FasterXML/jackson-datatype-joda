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
        case JsonTokenId.ID_STRING:
            String str = p.getText().trim();
            if (str.length() == 0) {
                return (LocalDate) getNullValue(ctxt);
            }
            // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
            //     some textual formats
            if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                    && _isValidTimestampString(str)) {
                return _fromTimestamp(ctxt, NumberInput.parseLong(str));
            }
            return _format.createParser(ctxt).parseLocalDate(str);
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_START_ARRAY:
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
        return (LocalDate) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected String, Number or JSON Array");
    }

    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        return new LocalDate(ts, tz);
    }
}
