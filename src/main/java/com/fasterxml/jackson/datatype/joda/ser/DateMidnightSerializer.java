package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateMidnight;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class DateMidnightSerializer
    extends JodaDateSerializerBase<DateMidnight>
{
    protected final static JacksonJodaFormat DEFAULT_FORMAT
        = new JacksonJodaFormat(DEFAULT_DATEONLY_FORMAT);
    
    public DateMidnightSerializer() { this(DEFAULT_FORMAT); }
    public DateMidnightSerializer(JacksonJodaFormat format) {
        super(DateMidnight.class, format);
    }

    @Override
    public DateMidnightSerializer withFormat(JacksonJodaFormat formatter) {
        return (_format == formatter) ? this : new DateMidnightSerializer(_format);
    }

    @Override
    public void serialize(DateMidnight value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // same as with other date-only values
            jgen.writeStartArray();
            jgen.writeNumber(value.year().get());
            jgen.writeNumber(value.monthOfYear().get());
            jgen.writeNumber(value.dayOfMonth().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "array" : "string", true);
    }
}