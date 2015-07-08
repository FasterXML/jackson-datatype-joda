package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.*;

import java.io.IOException;
import java.util.TimeZone;

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
                FormatConfig.DEFAULT_DATETIME_FORMAT);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateTimeDeserializer(_valueClass, format);
    }

    @Override
    public ReadableDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_NUMBER_INT) {
            TimeZone tz = ctxt.getTimeZone();
            DateTimeZone dtz = (tz == null) ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
            return new DateTime(p.getLongValue(), dtz);
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
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
                    throw ctxt.mappingException(String.format("Unknown DateTimeZone id '%s'", tzId));
                }
                str = str.substring(0, ix);

                // One more thing; do we have plain timestamp?
                if (_allDigits(str)) {
                    return new DateTime(Long.parseLong(str), tz);
                }
                return _format.createParser(ctxt)
                        .parseDateTime(str)
                        .withZone(tz);
            }
            
            // Not sure if it should use timezone or not...
            return _format.createParser(ctxt).parseDateTime(str);
        }
        throw ctxt.mappingException(handledType());
    }

    private static boolean _allDigits(String str)
    {
        for (int i = 0, len = str.length(); i < len; ++i) {
            int c = str.charAt(i);
            if (c > '9' | c < '0') {
                return false;
            }
        }
        return true;
    }
}
