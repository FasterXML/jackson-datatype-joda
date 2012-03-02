package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalDateTimeSerializer
    extends JodaSerializerBase<LocalDateTime>
{
    public LocalDateTimeSerializer() { super(LocalDateTime.class); }

    @Override
    public void serialize(LocalDateTime dt, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(dt.year().get());
            jgen.writeNumber(dt.monthOfYear().get());
            jgen.writeNumber(dt.dayOfMonth().get());
            jgen.writeNumber(dt.hourOfDay().get());
            jgen.writeNumber(dt.minuteOfHour().get());
            jgen.writeNumber(dt.secondOfMinute().get());
            jgen.writeNumber(dt.millisOfSecond().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(printLocalDateTime(dt));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint)
    {
        return createSchemaNode(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                ? "array" : "string", true);
    }
}