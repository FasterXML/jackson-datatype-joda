package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;

@SuppressWarnings("serial")
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
            TypeDeserializer typeDeserializer) throws IOException
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
        throws IOException
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
        throws IOException
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
        throws IOException
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
     * @since 2.23
     */
    @SuppressWarnings("unchecked")
    protected T _parseWithJoda(DeserializationContext ctxt, String value,
            Function<String, ? extends T> parser)
        throws IOException
    {
        try {
            return parser.apply(value);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return (T) ctxt.handleWeirdStringValue(handledType(), value,
                    "%s", e.getMessage());
        }
    }
}
