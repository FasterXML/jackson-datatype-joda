package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalDateTimeSerializer
    extends JodaDateSerializerBase<LocalDateTime>
{
    protected final static DateTimeFormatter DEFAULT_FORMAT = DEFAULT_LOCAL_DATETIME_FORMAT;

    public LocalDateTimeSerializer() { this(null); }
    public LocalDateTimeSerializer(Boolean useTimestamp) {
        super(LocalDateTime.class, useTimestamp);
    }

    @Override
    public LocalDateTimeSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return (useTimestamp == _useTimestamp) ? this
                : new LocalDateTimeSerializer(useTimestamp);
    }

    @Override
    public void serialize(LocalDateTime dt, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
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
            jgen.writeString(DEFAULT_FORMAT.print(dt));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "array" : "string", true);
    }
}