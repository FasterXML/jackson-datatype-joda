package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class DateTimeSerializer
    extends JodaSerializerBase<DateTime>
{
    public DateTimeSerializer() { super(DateTime.class); }

    private DateTimeFormatter format = null;

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(getDateTimeFormatter(provider).print(value));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint)
    {
        return createSchemaNode(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                ? "number" : "string", true);
    }

    private DateTimeFormatter getDateTimeFormatter(SerializerProvider provider) {
        if (format == null) {
            format = ISODateTimeFormat.dateTime().withZone(DateTimeZone.forTimeZone(provider.getTimeZone()));
        }
        return format;
    }
}