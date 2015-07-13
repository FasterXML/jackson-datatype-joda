package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

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
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
}
