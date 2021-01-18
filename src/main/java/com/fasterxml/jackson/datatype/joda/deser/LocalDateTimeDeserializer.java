package com.fasterxml.jackson.datatype.joda.deser;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateTimeDeserializer
    extends JodaDateDeserializerBase<LocalDateTime>
{
    public LocalDateTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATETIME_PARSER);
    }
    
    public LocalDateTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalDateTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateTimeDeserializer(format);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
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
        
        return (LocalDateTime) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
            "expected String, Number or JSON Array");
    }

    // @since 2.12
    protected LocalDateTime _fromString(final JsonParser p, final DeserializationContext ctxt,
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
        return _format.createParser(ctxt).parseLocalDateTime(value);
    }

    protected LocalDateTime _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        // [yyyy,mm,dd,hh,MM,ss,ms]
        JsonToken t = p.nextToken();
        LocalDateTime dt = null;
        do {
            if (!t.isNumeric()) { break; }
            int year = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT
            if (!t.isNumeric()) { break; }
            int month = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT
            if (!t.isNumeric()) { break; }
            int day = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT
            if (!t.isNumeric()) { break; }
            int hour = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT
            if (!t.isNumeric()) { break; }
            int minute = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT
            if (!t.isNumeric()) { break; }
            int second = p.getIntValue();
            t = p.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
            // let's leave milliseconds optional?
            int millisecond = 0;
            if (t.isNumeric()) { // VALUE_NUMBER_INT           
                millisecond = p.getIntValue();
                t = p.nextToken(); // END_ARRAY?
            }
            dt = new LocalDateTime(year, month, day, hour, minute, second, millisecond);                 
        } while (false); // bogus loop to allow break from within
        if (t == JsonToken.END_ARRAY) {
            return dt;
        }
        throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.END_ARRAY, "after LocalDateTime ints");
    }

    protected LocalDateTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        return new LocalDateTime(ts, tz);
    }
}
