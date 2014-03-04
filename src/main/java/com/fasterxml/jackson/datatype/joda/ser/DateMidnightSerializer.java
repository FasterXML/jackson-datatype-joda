package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class DateMidnightSerializer
    extends JodaDateSerializerBase<DateMidnight>
{
    protected final static DateTimeFormatter DEFAULT_FORMAT = DEFAULT_DATEONLY_FORMAT;

    public DateMidnightSerializer() { this(null); }
    public DateMidnightSerializer(Boolean useTimestamp) {
        super(DateMidnight.class, useTimestamp);
    }
    
    @Override
    public DateMidnightSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return (useTimestamp == _useTimestamp) ? this
                : new DateMidnightSerializer(useTimestamp);
    }

    @Override
    public void serialize(DateMidnight dt, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // same as with other date-only values
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