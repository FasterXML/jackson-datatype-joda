package tools.jackson.datatype.joda.deser;

import org.joda.time.Instant;

import tools.jackson.core.*;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.*;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Basic deserializer for {@link org.joda.time.ReadableDateTime} and its subtypes.
 * Accepts JSON String and Number values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 */
public class InstantDeserializer
    extends JodaDateDeserializerBase<Instant>
{
    public InstantDeserializer() {
        this(FormatConfig.DEFAULT_DATETIME_PARSER);
    }

    public InstantDeserializer(JacksonJodaDateFormat format) {
        super(Instant.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new InstantDeserializer(format);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT:
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getString());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        default:
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    // @since 2.12
    protected Instant _fromString(final JsonParser p, final DeserializationContext ctxt,
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
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }
        // 11-Sep-2018, tatu: `DateTimeDeserializer` allows timezone inclusion in brackets;
        //    should that be checked here too?
        return Instant.parse(value, _format.createParser(ctxt));
    }

    // @since 2.12
    protected Instant _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new Instant(ts);
    }
}
