package tools.jackson.datatype.joda.deser;

import org.joda.time.LocalTime;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalTimeDeserializer
    extends JodaDateDeserializerBase<LocalTime>
{
    public LocalTimeDeserializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PARSER);
    }

    public LocalTimeDeserializer(JacksonJodaDateFormat format) {
        super(LocalTime.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new LocalTimeDeserializer(format);
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return new LocalTime(p.getLongValue());            
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getString());
        case JsonTokenId.ID_START_OBJECT:
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        default:
        }
        // [HH,MM,ss,ms?]
        if (p.isExpectedStartArrayToken()) {
            return _fromArray(p, ctxt);
        }
        return (LocalTime) ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p,
                "expected JSON Array, String or Number");
    }

    // @since 2.12
    protected LocalTime _fromString(final JsonParser p, final DeserializationContext ctxt,
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
            return new LocalTime(NumberInput.parseLong(value));
        }
        return _format.createParser(ctxt).parseLocalTime(value);
    }

    // @since 2.12
    protected LocalTime _fromArray(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        p.nextToken(); // VALUE_NUMBER_INT 
        int hour = p.getIntValue(); 
        p.nextToken(); // VALUE_NUMBER_INT
        int minute = p.getIntValue();
        p.nextToken(); // VALUE_NUMBER_INT
        int second = p.getIntValue();
        p.nextToken(); // VALUE_NUMBER_INT | END_ARRAY
        int millis = 0;
        if (p.currentToken() != JsonToken.END_ARRAY) {
            millis = p.getIntValue();
            p.nextToken(); // END_ARRAY?
        }
        if (p.currentToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.END_ARRAY, "after LocalTime ints");
        }
        return new LocalTime(hour, minute, second, millis);
    }
}
