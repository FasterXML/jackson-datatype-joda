package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Basic deserializer for {@link ReadableDateTime} and its subtypes.
 * Accepts JSON String and Number values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 */
public class DateTimeDeserializer
    extends JodaDateDeserializerBase<ReadableInstant>
{
    private static final long serialVersionUID = 1L;

    public DateTimeDeserializer(Class<?> cls, JacksonJodaDateFormat format) {
        super(cls, format);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls)
    {
        return (JsonDeserializer<T>) new DateTimeDeserializer(cls,
                FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateTimeDeserializer(_valueClass, format);
    }

    @Override
    public ReadableInstant deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.currentToken();
        
        if (t == JsonToken.VALUE_NUMBER_INT) {
            DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
            return new DateTime(p.getLongValue(), tz);
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.length() == 0) {
                return null;
            }
            // 08-Jul-2015, tatu: as per [datatype-joda#44], optional TimeZone inclusion
            // NOTE: on/off feature only for serialization; on deser should accept both
            int ix = str.indexOf('[');
            if (ix > 0) {
                int ix2 = str.lastIndexOf(']');
                String tzId = (ix2 < ix)
                        ? str.substring(ix+1)
                        : str.substring(ix+1, ix2);
                DateTimeZone tz;
                try {
                    tz = DateTimeZone.forID(tzId);
                } catch (IllegalArgumentException e) {
                    ctxt.reportInputMismatch(handledType(), "Unknown DateTimeZone id '%s'", tzId);
                    tz = null; // never gets here
                }
                str = str.substring(0, ix);

                // 12-Jul-2015, tatu: Initially planned to support "timestamp[zone-id]"
                //    format as well as textual, but since JSR-310 datatype (Java 8 datetime)
                //    does not support it, was left out of 2.6.

                /*
                // One more thing; do we have plain timestamp?
                if (_allDigits(str)) {
                    return new DateTime(Long.parseLong(str), tz);
                }
                */

                DateTime result = _format.createParser(ctxt)
                        .withZone(tz)
                        .parseDateTime(str)
                        ;
                // 23-Jul-2017, tatu: As per [datatype-joda#93] only override tz if allowed to
                if (_format.shouldAdjustToContextTimeZone(ctxt)) {
                    result = result.withZone(_format.getTimeZone());
                }
                return result;
            }
            // Not sure if it should use timezone or not...
            // 15-Sep-2015, tatu: impl of 'createParser()' SHOULD handle all timezone/locale setup
            return _format.createParser(ctxt).parseDateTime(str);
        }
        return _handleNotNumberOrString(p, ctxt);
    }
}
