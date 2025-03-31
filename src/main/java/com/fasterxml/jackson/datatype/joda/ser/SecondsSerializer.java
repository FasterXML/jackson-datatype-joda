package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Seconds;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Seconds} class.
 *
 * @since 2.18.4
 */
public class SecondsSerializer
    extends JodaSerializerBase<Seconds>
{

    private static final long serialVersionUID = 1L;

    public SecondsSerializer() {
        super(Seconds.class);
    }

    @Override
    public void serialize(Seconds value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getSeconds());
    }
}
