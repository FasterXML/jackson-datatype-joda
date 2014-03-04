package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class DateTimeSerializer
    extends JodaDateSerializerBase<DateTime>
{
    protected final static DateTimeFormatter DEFAULT_FORMAT
        = ISODateTimeFormat.dateTime().withZoneUTC();
    
    protected final DateTimeFormatter _formatter;

    protected final TimeZone _jdkTimezone;
    
    public DateTimeSerializer() { this(null, null); }
    public DateTimeSerializer(Boolean useNumeric, TimeZone jdkTimezone) {
        super(DateTime.class, useNumeric);
        _jdkTimezone = jdkTimezone;
        if (jdkTimezone == null) {
            _formatter = DEFAULT_FORMAT;
        } else {
            _formatter = DEFAULT_FORMAT.withZone(DateTimeZone.forTimeZone(jdkTimezone));
        }
    }

    @Override
    public DateTimeSerializer withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone) {
        return new DateTimeSerializer(useTimestamp, jdkTimezone);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(getDateTimeFormatter(provider).print(value));
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "number" : "string", true);
    }

    private DateTimeFormatter getDateTimeFormatter(SerializerProvider provider)
    {
        DateTimeFormatter formatter = _formatter;
        TimeZone tz = _jdkTimezone;
        if (tz == null) {
            TimeZone defTz = provider.getTimeZone();
            if (defTz != null && !defTz.equals(tz)) {
                formatter = formatter.withZone(DateTimeZone.forTimeZone(defTz));
            }
        }
        return formatter;
    }
}
