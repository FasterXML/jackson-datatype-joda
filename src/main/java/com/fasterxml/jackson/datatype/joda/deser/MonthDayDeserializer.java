package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.MonthDay;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

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
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return (MonthDay) getNullValue(ctxt);
            }
            return MonthDay.parse(str, _format.createParser(ctxt));
        } else if (p.hasToken(JsonToken.START_OBJECT)) {
            JsonNode treeNode = p.readValueAsTree();
            int month = treeNode.path("monthOfYear").asInt(Integer.MIN_VALUE);
            int day = treeNode.path("dayOfMonth").asInt(Integer.MIN_VALUE);
            if (month >= 0 && day >= 0) {
                return new MonthDay(month, day);
            }
        }
        return (MonthDay) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON String");
    }
}
