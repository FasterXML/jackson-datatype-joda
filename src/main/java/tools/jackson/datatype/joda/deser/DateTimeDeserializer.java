package tools.jackson.datatype.joda.deser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

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

    /**
     * @since 2.14 (to allow use via annotations)
     */
    public DateTimeDeserializer() {
        super(DateTime.class, FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> ValueDeserializer<T> forType(Class<T> cls)
    {
        return (ValueDeserializer<T>) new DateTimeDeserializer(cls,
                FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new DateTimeDeserializer(_valueClass, format);
    }

    @Override
    public ReadableInstant deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getString());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    // @since 2.12
    protected ReadableInstant _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // 08-Jul-2015, tatu: as per [datatype-joda#44], optional TimeZone inclusion
        // NOTE: on/off feature only for serialization; on deser should accept both
        // 07-Oct-2026: [datatype-joda#191] zone id suffix has to close the value;
        //   and is the LAST '[...]' (custom patterns may contain literal brackets)
        int ix = value.endsWith("]") ? value.lastIndexOf('[') : -1;
        if (ix > 0) {
            DateTimeZone tz;
            String tzId = value.substring(ix+1, value.length() - 1);
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
        final int openIx = value.indexOf('[');
        if (openIx > 0) {
            // 07-Oct-2026: [datatype-joda#191] Not a valid zone id suffix, but may still
            //   be valid for a custom pattern with literal '['
            try {
                return _format.createParser(ctxt).parseDateTime(value);
            } catch (IllegalArgumentException e) {
                final String msg = (value.indexOf(']', openIx) < 0)
                        ? "Malformed DateTimeZone id suffix: missing closing ']'"
                        : "Malformed DateTimeZone id suffix: unexpected content after closing ']'";
                return (ReadableInstant) ctxt.handleWeirdStringValue(handledType(), value, msg);
            }
        }
        return _format.createParser(ctxt).parseDateTime(value);
    }

    protected DateTime _fromTimestamp(DeserializationContext ctxt, long ts) {
        DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                : DateTimeZone.forTimeZone(ctxt.getTimeZone());
        return new DateTime(ts, tz);
    }
}
