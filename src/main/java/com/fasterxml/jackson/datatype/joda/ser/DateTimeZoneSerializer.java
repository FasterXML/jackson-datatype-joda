package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void serializeWithType(DateTimeZone value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(value, gen, DateTimeZone.class);
        serialize(value, gen, provider);
        typeSer.writeTypeSuffixForScalar(value, gen);
    }
}
