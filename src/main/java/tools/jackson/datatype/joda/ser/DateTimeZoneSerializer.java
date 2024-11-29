package tools.jackson.datatype.joda.ser;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsontype.TypeSerializer;

import org.joda.time.DateTimeZone;

public class DateTimeZoneSerializer extends JodaSerializerBase<DateTimeZone>
{
    public DateTimeZoneSerializer() { super(DateTimeZone.class); }

    @Override
    public void serialize(DateTimeZone value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        g.writeString(value.getID());
    }

    // as per [datatype-joda#82], need to ensure we will indicate nominal, NOT physical type:
    @Override
    public void serializeWithType(DateTimeZone value, JsonGenerator g,
            SerializationContext ctxt, TypeSerializer typeSer)
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
