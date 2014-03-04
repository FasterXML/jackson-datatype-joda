package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalTimeSerializer
    extends JodaDateSerializerBase<LocalTime>
{
    protected final static DateTimeFormatter DEFAULT_FORMAT = DEFAULT_TIMEONLY_FORMAT;

    public LocalTimeSerializer() { this(null); }
    public LocalTimeSerializer(Boolean useTimestamp) {
        super(LocalTime.class, useTimestamp);
    }
    
    @Override
    public LocalTimeSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return (useTimestamp == _useTimestamp) ? this
                : new LocalTimeSerializer(useTimestamp);
    }

    @Override
    public void serialize(LocalTime tm, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(tm.hourOfDay().get());
            jgen.writeNumber(tm.minuteOfHour().get());
            jgen.writeNumber(tm.secondOfMinute().get());
            jgen.writeNumber(tm.millisOfSecond().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(DEFAULT_FORMAT.print(tm));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "array" : "string", true);
    }
}