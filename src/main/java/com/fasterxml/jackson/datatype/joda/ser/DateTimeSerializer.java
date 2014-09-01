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
            // Unfortunately, I don't see a way to include milliseconds since
            // the epoch in a DateTimeFormatter or this code (and the
            // deserializer) could likely be simpler...Perhaps like this:
            //
            // // The provider has a time zone, but let's not use it.  Use the time
            // // zone from value instead since the goal is to preserve that across de/serialization.
            // DateTimeFormatter providerFormatter = _format.createFormatter(provider);
            // DateTimeFormatter valueFormatter = providerFormatter.withZone(value.getZone());
            // jgen.writeString(valueFormatter.print(value));
            //
            // Instead, if someone has set an explicit format, use it.
            if (_format != DEFAULT_FORMAT) {
                jgen.writeString(_format.createFormatter(provider).print(value));
            } else {
                // Store value's time zone explicitly in addition to some
                // representation of the time so it survives a de/serialization
                // round trip.  Assume that this is the desired behavior for all
                // DateTime instances.  If someone only cares about the instant
                // in time, suggest s/he use Instant.
                StringBuilder timeWithZone = new StringBuilder();

                // Store the time in UTC to avoid the ambiguity that occurs on a
                // "fall back" DST transition.  For example, on November 3, 2013
                // in America/Los_Angeles, 1:00a happens twice...once at 8:00:00
                // UTC (before falling back) and once at 9:00:00 UTC (after
                // falling back).  From there we have a choice between an
                // ISO8601 string or milliseconds since the epoch.  I'm going
                // with milliseconds since the epoch since it's more compact.
                timeWithZone.append(value.getMillis());
                timeWithZone.append('/');
                timeWithZone.append(value.getZone().toString());
                jgen.writeString(timeWithZone.toString());
            }
        }
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode(_useTimestamp(provider) ? "number" : "string", true);
    }
}
