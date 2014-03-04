package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalDateSerializer
    extends JodaDateSerializerBase<LocalDate>
{
    protected final static DateTimeFormatter DEFAULT_FORMAT = DEFAULT_DATEONLY_FORMAT;

    public LocalDateSerializer() { this(null); }
    public LocalDateSerializer(Boolean useTimestamp) {
        super(LocalDate.class, useTimestamp);
    }

    @Override
    public LocalDateSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return (useTimestamp == _useTimestamp) ? this
                : new LocalDateSerializer(useTimestamp);
    }
    
    @Override
    public void serialize(LocalDate dt, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(dt.year().get());
            jgen.writeNumber(dt.monthOfYear().get());
            jgen.writeNumber(dt.dayOfMonth().get());
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
