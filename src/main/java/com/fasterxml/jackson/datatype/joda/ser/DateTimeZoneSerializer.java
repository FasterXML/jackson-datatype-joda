package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import org.joda.time.DateTimeZone;

public class DateTimeZoneSerializer extends JodaSerializerBase<DateTimeZone>
{
    public DateTimeZoneSerializer() { super(DateTimeZone.class); }

    @Override
    public void serialize(DateTimeZone value, JsonGenerator g, SerializerProvider ctxt)
        throws JacksonException
    {
        g.writeString(value.getID());
    }

    // as per [datatype-joda#82], need to ensure we will indicate nominal, NOT physical type:
    @Override
    public void serializeWithType(DateTimeZone value, JsonGenerator g,
            SerializerProvider ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, DateTimeZone.class, JsonToken.VALUE_STRING));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        visitor.expectStringFormat(typeHint);
    }
}
