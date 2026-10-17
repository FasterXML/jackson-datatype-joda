package tools.jackson.datatype.joda.deser;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateDeserializer
    extends JodaDateDeserializerBase<LocalDate>
{
    public LocalDateDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT);
    }
    
    public LocalDateDeserializer(JacksonJodaDateFormat format) {
        super(LocalDate.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalDateDeserializer(format);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
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
        default:
        }
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalDate) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected String, Number or JSON Array");
    }

    // @since 2.12
    protected LocalDate _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value), value);
        }
        final DateTimeFormatter parser = _format.createParser(ctxt);
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return parser.parseLocalDate(value);
        } catch (IllegalArgumentException e) {
            return (LocalDate) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        }
    }

    // @since 2.12
    protected LocalDate _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        // [yyyy,mm,dd] or ["yyyy","mm","dd"]
        final String msg = "expected 3 int values for LocalDate";
        int year = _nextArrayInt(p, ctxt, msg);
        int month = _nextArrayInt(p, ctxt, msg);
        int day = _nextArrayInt(p, ctxt, msg);
        if (p.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.END_ARRAY, "after LocalDate ints");
        }
        try {
            return new LocalDate(year, month, day);
        } catch (IllegalFieldValueException e) {
            throw _invalidArrayValue(ctxt, e);
        }
    }

    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts) {
        return _fromTimestamp(ctxt, ts, null);
    }

    // @since 3.3
    protected LocalDate _fromTimestamp(DeserializationContext ctxt, long ts, String text) {
        // [datatype-joda#199]: zone offset addition for edge-of-long timestamp can
        //   overflow (or result fall outside year range); do not let raw Joda exception escape
        return _fromTimestamp(ctxt, ts, text, LocalDate::new);
    }
}
