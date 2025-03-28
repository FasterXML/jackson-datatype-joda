package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Days;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DaysSerializer
    extends JodaSerializerBase<Days>
{
    private static final long serialVersionUID = 1L;

    public DaysSerializer() {
        super(Days.class);
    }

    @Override
    public void serialize(Days value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getDays());
    }
}
