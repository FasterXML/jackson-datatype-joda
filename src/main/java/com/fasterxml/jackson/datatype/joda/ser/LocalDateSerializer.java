package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class LocalDateSerializer
    extends JodaDateSerializerBase<LocalDate>
{
    protected final static JacksonJodaDateFormat DEFAULT_FORMAT
        = new JacksonJodaDateFormat(DEFAULT_DATEONLY_FORMAT);

    public LocalDateSerializer() { this(DEFAULT_FORMAT); }
    public LocalDateSerializer(JacksonJodaDateFormat format) {
        super(LocalDate.class, format, true,
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public LocalDateSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new LocalDateSerializer(formatter);
    }
    
    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_useTimestamp(provider)) {
            // Timestamp here actually means an array of values
            jgen.writeStartArray();
            jgen.writeNumber(value.year().get());
            jgen.writeNumber(value.monthOfYear().get());
            jgen.writeNumber(value.dayOfMonth().get());
            jgen.writeEndArray();
        } else {
            jgen.writeString(_format.createFormatter(provider).print(value));
        }
    }
}
