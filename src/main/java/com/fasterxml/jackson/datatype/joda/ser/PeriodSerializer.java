package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
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
}
