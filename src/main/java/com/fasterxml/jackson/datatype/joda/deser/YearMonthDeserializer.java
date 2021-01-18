package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.YearMonth;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * A Jackson deserializer for Joda YearMonth objects.
 * <p>
 * Expects a string value compatible with YearMonth's parse operation.
 */
public class YearMonthDeserializer extends JodaDateDeserializerBase<YearMonth>
{
    public YearMonthDeserializer() {
        this(FormatConfig.DEFAULT_YEAR_MONTH_FORMAT);
    }

    public YearMonthDeserializer(JacksonJodaDateFormat format) {
        super(YearMonth.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new YearMonthDeserializer(format);
    }

    @Override
    public YearMonth deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        return (YearMonth) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON String");
    }

    // @since 2.12
    protected YearMonth _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        return YearMonth.parse(value, _format.createParser(ctxt));
    }
}
