package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a {@link ReadablePeriod} using Joda default formatting.
 *<p>
 * TODO: allow serialization as an array of numbers, for numeric ("timestamp")
 * notation?
 */
public final class PeriodSerializer extends JodaSerializerBase<ReadablePeriod>
{
    protected final PeriodFormatter defaultFormat = ISOPeriodFormat.standard();

    public PeriodSerializer() { super(ReadablePeriod.class); }

    @Override
    public void serialize(ReadablePeriod value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
        JsonProcessingException
    {
        jgen.writeString(defaultFormat.print(value));
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode("string", true);
    }
}
