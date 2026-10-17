package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.function.Function;

import tools.jackson.core.*;
import tools.jackson.core.io.NumberInput;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.ClassUtil;

abstract class JodaDeserializerBase<T> extends StdScalarDeserializer<T>
{
    protected JodaDeserializerBase(Class<?> cls) {
        super(cls);
    }

    protected JodaDeserializerBase(JodaDeserializerBase<?> src) {
        super(src);
    }
    
    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    @Override
    public LogicalType logicalType() { return LogicalType.DateTime; }

    // @since 2.12
    protected boolean _isValidTimestampString(String str) {
        // 14-Jul-2020, tatu: Need to support "numbers as Strings" for data formats
        //    that only have String values for scalars (CSV, Properties, XML)
        // NOTE: we do allow negative values, but has to fit in 64-bits:
        if (!_isIntNumber(str)) {
            return false;
        }
        // 16-Oct-2026: `inLongRange()` expects digits only, so must skip
        //   leading sign (if any) for 19-digit values to be accepted
        final char c = str.charAt(0);
        if (c == '-' || c == '+') {
            return NumberInput.inLongRange(str.substring(1), c == '-');
        }
        return NumberInput.inLongRange(str, false);
    }

    /**
     * Helper method for specific case of deserialization
     * from empty or blank String.
     *
     * @since 2.12
     */
    @SuppressWarnings("unchecked")
    protected T _fromEmptyString(JsonParser p, DeserializationContext ctxt,
            String str)
        throws JacksonException
    {
        final CoercionAction act = _checkFromStringCoercion(ctxt, str);
        switch (act) { // note: Fail handled above
        case AsEmpty:
            return (T) getEmptyValue(ctxt);
        case TryConvert:
        case AsNull:
        default:
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public T _handleNotNumberOrString(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        final JavaType type = getValueType(ctxt);
        final JsonToken t = p.currentToken();
        return (T) ctxt.handleUnexpectedToken(type, t, p,
                String.format("Cannot deserialize value of type %s from %s (token `JsonToken.%s`): expected Number or String",
                        ClassUtil.getTypeDescription(type),
                        JsonToken.valueDescFor(t),
                        p.currentToken()));
    }

    @SuppressWarnings("unchecked")
    public T _handleNotString(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        final JavaType type = getValueType(ctxt);
        final JsonToken t = p.currentToken();
        return (T) ctxt.handleUnexpectedToken(type, t, p,
                String.format("Cannot deserialize value of type %s from %s (token `JsonToken.%s`): expected String",
                        ClassUtil.getTypeDescription(type),
                        JsonToken.valueDescFor(t),
                        p.currentToken()));
    }

    /**
     * Helper method for parsing given String value using Joda functionality,
     * so that Joda failures -- {@link IllegalArgumentException} (including
     * {@link org.joda.time.IllegalFieldValueException}) and {@link ArithmeticException}
     * (for overflows) -- are reported via
     * {@link DeserializationContext#handleWeirdStringValue} instead of escaping
     * as raw Joda exceptions (see [datatype-joda#195], [datatype-joda#200]).
     *
     * @since 3.3
     */
    @SuppressWarnings("unchecked")
    protected T _parseString(DeserializationContext ctxt, String value,
            Function<String, ? extends T> parser)
        throws JacksonException
    {
        try {
            return parser.apply(value);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return (T) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        }
    }

    /**
     * Helper method called to handle cases of Joda functionality throwing an
     * {@link IOException} due to parse/generation issue, distinct from general
     * input/output issues underlying streaming might throw.
     * Currently handling does not differ but it is possible this might change in
     * future to, for example, give more information on type of failure.
     *
     * @param e {@link IOException} thrown by one of Joda methods (as opposed to other
     *    kinds of input/output problems)
     *
     * @return Suitable wrapped exception instance to throw
     */
    protected JacksonException _wrapJodaFailure(DeserializationContext ctxt, IOException e) {
        // 18-Jan-2021, tatu: Start by simply reusing functionality of more
        //   general handling, for now
        return _wrapIOFailure(ctxt, e);
    }
}
