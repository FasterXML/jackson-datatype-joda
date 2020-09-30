package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;

import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.MonthDay;

/**
 * A Jackson deserializer for Joda MonthDay objects.
 * <p>
 * Expects a string value compatible with MonthDay's parse operation.
 */
public class MonthDayDeserializer extends JodaDateDeserializerBase<MonthDay>
{
    public MonthDayDeserializer() {
        this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT);
    }

    public MonthDayDeserializer(JacksonJodaDateFormat format) {
        super(MonthDay.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new MonthDayDeserializer(format);
    }

    @Override
    public MonthDay deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        return (MonthDay) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON String");
    }

    // @since 2.12
    protected MonthDay _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return (MonthDay) getNullValue(ctxt);
        }
        return MonthDay.parse(value, _format.createParser(ctxt));
    }
}
