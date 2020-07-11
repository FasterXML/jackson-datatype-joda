package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.YearMonth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

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
    public YearMonth deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException
    {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
            return YearMonth.parse(str, _format.createParser(ctxt));
        } else if (t == JsonToken.START_OBJECT) {
            JsonNode treeNode = p.readValueAsTree();
            int year = treeNode.path("year").asInt(Integer.MIN_VALUE);
            int month = treeNode.path("monthOfYear").asInt(Integer.MIN_VALUE);
            if (year != Integer.MIN_VALUE && month != Integer.MIN_VALUE) {
                return new YearMonth(year, month);
            }
        }
        return (YearMonth) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON String");
    }
}
