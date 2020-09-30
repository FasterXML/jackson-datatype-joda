package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
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
        return _isIntNumber(str) && NumberInput.inLongRange(str, (str.charAt(0) == '-'));
    }

    @SuppressWarnings("unchecked")
    public T _handleNotNumberOrString(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        final JavaType type = getValueType(ctxt);
        return (T) ctxt.handleUnexpectedToken(type, p.currentToken(), p,
                String.format("Cannot deserialize value of type %s from `JsonToken.%s`: expected Number or String",
                        ClassUtil.getTypeDescription(type), p.currentToken()));
    }
}
