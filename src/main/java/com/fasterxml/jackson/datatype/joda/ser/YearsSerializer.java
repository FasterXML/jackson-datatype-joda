package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Years;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Years} class.
 *
 * @since 2.18.4
 */
public class YearsSerializer
    extends JodaSerializerBase<Years>
{

    private static final long serialVersionUID = 1L;

    public YearsSerializer() {
        super(Years.class);
    }

    @Override
    public void serialize(Years value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getYears());
    }
}
