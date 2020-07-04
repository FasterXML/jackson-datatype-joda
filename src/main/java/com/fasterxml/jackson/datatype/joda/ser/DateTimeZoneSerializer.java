package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class DateTimeZoneSerializer extends JodaSerializerBase<DateTimeZone>
{
    private static final long serialVersionUID = 1L;

    public DateTimeZoneSerializer() { super(DateTimeZone.class); }

    @Override
    public void serialize(DateTimeZone value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        gen.writeString(value.getID());
    }

    // as per [datatype-joda#82], need to ensure we will indicate nominal, NOT physical type:
    @Override
    public void serializeWithType(DateTimeZone value, JsonGenerator g,
            SerializerProvider provider, TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, DateTimeZone.class, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
        visitor.expectStringFormat(typeHint);
    }
}
