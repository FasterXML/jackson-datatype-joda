package tools.jackson.datatype.joda.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.MonthDay;

/**
 * A Jackson deserializer for Joda MonthDay objects.
 * <p>
 * Expects a string value compatible with MonthDay's parse operation.
 */
public class MonthDayDeserializer extends JodaDateDeserializerBase<MonthDay>
{
    public MonthDayDeserializer() {
        this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT);
    }

    public MonthDayDeserializer(JacksonJodaDateFormat format) {
        super(MonthDay.class, format);
    }

    @Override
    public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format) {
        return new MonthDayDeserializer(format);
    }

    @Override
    public MonthDay deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getString());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            // fall through
        }
        return _handleNotString(p, ctxt);
    }

    // @since 2.12
    protected MonthDay _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        // [datatype-joda#195]: do not let raw Joda exception escape
        try {
            return MonthDay.parse(value, _format.createParser(ctxt));
        } catch (IllegalArgumentException e) {
            throw _invalidStringValue(ctxt, value, e);
        }
    }
}
