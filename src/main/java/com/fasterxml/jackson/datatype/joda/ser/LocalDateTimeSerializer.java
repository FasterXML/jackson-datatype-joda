package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalDateTimeSerializer
    extends JodaDateSerializerBase<LocalDateTime>
{
    protected final static JacksonJodaFormat DEFAULT_FORMAT
        = new JacksonJodaFormat(DEFAULT_LOCAL_DATETIME_FORMAT);

    public LocalDateTimeSerializer() { this(DEFAULT_FORMAT); }
    public LocalDateTimeSerializer(JacksonJodaFormat format) {
        super(LocalDateTime.class, format);
    }

    @Override
    public LocalDateTimeSerializer withFormat(JacksonJodaFormat formatter) {
        return (_format == formatter) ? this : new LocalDateTimeSerializer(formatter);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(value.year().get());
            jgen.writeNumber(value.monthOfYear().get());
            jgen.writeNumber(value.dayOfMonth().get());
            jgen.writeNumber(value.hourOfDay().get());
            jgen.writeNumber(value.minuteOfHour().get());
            jgen.writeNumber(value.secondOfMinute().get());
            jgen.writeNumber(value.millisOfSecond().get());
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