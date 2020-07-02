package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
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
            TypeDeserializer typeDeserializer) throws IOException
    {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    @Override
    public LogicalType logicalType() { return LogicalType.DateTime; }

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
