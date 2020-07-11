package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
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
        throws IOException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            {
                String str = p.getText().trim();
                return (str.length() == 0) ? null
                        : _format.createParser(ctxt).parseLocalDateTime(str);
            }
        case JsonTokenId.ID_NUMBER_INT:
            {
                DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
                return new LocalDateTime(p.getLongValue(), tz);
            }
        case JsonTokenId.ID_START_ARRAY:
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
            throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY, "after LocalDateTime ints");
        case JsonTokenId.ID_START_OBJECT:
            JsonNode treeNode = p.readValueAsTree();
            int year = treeNode.path("year").asInt(Integer.MIN_VALUE);
            int month = treeNode.path("monthOfYear").asInt(Integer.MIN_VALUE);
            int day = treeNode.path("dayOfMonth").asInt(Integer.MIN_VALUE);
            int hourOfDay = treeNode.path("hourOfDay").asInt(Integer.MIN_VALUE);
            int minuteOfHour = treeNode.path("minuteOfHour").asInt(Integer.MIN_VALUE);
            int secondOfMinute = treeNode.path("secondOfMinute").asInt(Integer.MIN_VALUE);
            int millisOfSecond = treeNode.path("millisOfSecond").asInt(0); // optional and defaults to zero
            if (year != Integer.MIN_VALUE && month >= 0 && day >= 0
                    && hourOfDay >= 0 && minuteOfHour >= 0 && secondOfMinute >= 0) {
                return new LocalDateTime(year, month, day,
                        hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
            }
        default:
        }
        return (LocalDateTime) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
            "expected String, Number or JSON Array");
    }
}
