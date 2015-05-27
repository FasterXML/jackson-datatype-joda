package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateDeserializer
    extends JodaDeserializerBase<LocalDate>
{
    private static final long serialVersionUID = 1L;

    final static DateTimeFormatter parser = ISODateTimeFormat.localDateParser();

    public LocalDateDeserializer() { super(LocalDate.class); }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        // [yyyy,mm,dd] or ["yyyy","mm","dd"]
        if (jp.isExpectedStartArrayToken()) {
            jp.nextToken(); // VALUE_NUMBER_INT or VALUE_STRING
            int year = new Integer(jp.getText());
            jp.nextToken(); // VALUE_NUMBER_INT or VALUE_STRING
            int month = new Integer(jp.getText());
            jp.nextToken(); // VALUE_NUMBER_INT or VALUE_STRING
            int day = new Integer(jp.getText());
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "after LocalDate ints");
            }
            return new LocalDate(year, month, day);
        }
        switch (jp.getCurrentToken()) {
        case VALUE_NUMBER_INT:
            return new LocalDate(jp.getLongValue());            
        case VALUE_STRING:
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return parser.parseLocalDate(str);
        default:
        }
        throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "expected JSON Array, String or Number");
    }
}