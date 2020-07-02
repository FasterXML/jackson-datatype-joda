package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Note: Joda has <code>DateMidnight</code> deprecated since at least 2.4,
 * but we still support it for now.
 *
 * @deprecated Since 2.7
 */
@Deprecated // since Jackson 2.7 (and Joda 2.4)
public class DateMidnightDeserializer
    extends JodaDateDeserializerBase<DateMidnight>
{
    private static final long serialVersionUID = 1L;

    // final static DateTimeFormatter parser =
    // ISODateTimeFormat.localDateParser();

    public DateMidnightDeserializer() {
        this(FormatConfig.DEFAULT_DATEONLY_FORMAT);
    }

    public DateMidnightDeserializer(JacksonJodaDateFormat format) {
        super(DateMidnight.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateMidnightDeserializer(format);
    }

    @Override
    public DateMidnight deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
    	
        // We'll accept either long (timestamp) or array:
        if (p.isExpectedStartArrayToken()) {
            p.nextToken(); // VALUE_NUMBER_INT
            int year = p.getIntValue();
            p.nextToken(); // VALUE_NUMBER_INT
            int month = p.getIntValue();
            p.nextToken(); // VALUE_NUMBER_INT
            int day = p.getIntValue();
            if (p.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY,
                        "after DateMidnight ints");
            }
            DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());

            return new DateMidnight(year, month, day, tz);
        }
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return new DateMidnight(p.getLongValue());
        case JsonTokenId.ID_STRING:
            String str = p.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            LocalDate local = _format.createParser(ctxt).parseLocalDate(str);
            if (local == null) {
                return null;
            }
            return local.toDateMidnight();
        default:
        }
        throw ctxt.wrongTokenException(p, handledType(), JsonToken.START_ARRAY,
                "expected JSON Array, Number or String");
    }
}