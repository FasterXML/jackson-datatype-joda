package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
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
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    // @since 2.12
    protected ReadableInstant _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // 08-Jul-2015, tatu: as per [datatype-joda#44], optional TimeZone inclusion
        // NOTE: on/off feature only for serialization; on deser should accept both
        int ix = value.indexOf('[');
        if (ix > 0) {
            DateTimeZone tz;
            int ix2 = value.lastIndexOf(']');
            String tzId = (ix2 < ix)
                    ? value.substring(ix+1)
                    : value.substring(ix+1, ix2);
            try {
                tz = DateTimeZone.forID(tzId);
            } catch (IllegalArgumentException e) {
                ctxt.reportInputMismatch(getValueType(ctxt), "Unknown DateTimeZone id '%s'", tzId);
                tz = null; // never gets here
            }
            value = value.substring(0, ix);

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
                    .parseDateTime(value)
                    ;
            // 23-Jul-2017, tatu: As per [datatype-joda#93] only override tz if allowed to
            if (_format.shouldAdjustToContextTimeZone(ctxt)) {
                result = result.withZone(_format.getTimeZone());
            }
            return result;
        }
        // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }

        // Not sure if it should use timezone or not...
        // 15-Sep-2015, tatu: impl of 'createParser()' SHOULD handle all timezone/locale setup
        return _format.createParser(ctxt).parseDateTime(value);
    }

    protected DateTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        return new DateTime(ts, tz);
    }
}
