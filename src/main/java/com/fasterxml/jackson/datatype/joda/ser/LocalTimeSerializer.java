package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalTimeSerializer
    extends JodaSerializerBase<LocalTime>
{
    final static DateTimeFormatter format = ISODateTimeFormat.time();

    public LocalTimeSerializer() { super(LocalTime.class); }

    @Override
    public void serialize(LocalTime tm, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(tm.hourOfDay().get());
            jgen.writeNumber(tm.minuteOfHour().get());
            jgen.writeNumber(tm.secondOfMinute().get());
            jgen.writeNumber(tm.millisOfSecond().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(format.print(tm));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint)
    {
        return createSchemaNode(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                ? "array" : "string", true);
    }
}