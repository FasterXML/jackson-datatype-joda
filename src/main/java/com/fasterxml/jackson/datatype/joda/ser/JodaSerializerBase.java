package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

abstract class JodaSerializerBase<T> extends StdSerializer<T>
{
    protected JodaSerializerBase(Class<T> cls) { super(cls); }

    @Override
    public void serializeWithType(T value, JsonGenerator g, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        // NOTE: we do not actually know the exact shape (or, rather, it varies by settings
        // and so should not claim particular shape) -- but need to make sure NOT to report
        // as `Shape.OBJECT` or `Shape.ARRAY`
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }
}
