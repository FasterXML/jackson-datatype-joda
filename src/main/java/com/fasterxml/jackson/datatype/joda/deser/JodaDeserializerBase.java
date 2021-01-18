package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.NumberInput;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;

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
        return _isIntNumber(str) && NumberInput.inLongRange(str, (str.charAt(0) == '-'));
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
        return (T) ctxt.handleUnexpectedToken(type, p.currentToken(), p,
                String.format("Cannot deserialize value of type %s from `JsonToken.%s`: expected Number or String",
                        ClassUtil.getTypeDescription(type), p.currentToken()));
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
    protected JacksonException _wrapJodaFailure(IOException e) {
        // 18-Jan-2021, tatu: Start by simply reusing functionality of more
        //   general handling, for now
        return _wrapIOFailure(e);
    }
}
