package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.format.ISODateTimeFormat;

public final class DateTimeSerializer
    extends JodaDateSerializerBase<DateTime>
{
    protected final static JacksonJodaFormat DEFAULT_FORMAT
        = new JacksonJodaFormat(ISODateTimeFormat.dateTime().withZoneUTC());
    
    public DateTimeSerializer() { this(DEFAULT_FORMAT); }
    public DateTimeSerializer(JacksonJodaFormat format) {
        super(DateTime.class, format);
    }

    @Override
    public DateTimeSerializer withFormat(JacksonJodaFormat formatter) {
        return (_format == formatter) ? this : new DateTimeSerializer(formatter);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "number" : "string", true);
    }
}
