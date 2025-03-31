package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Minutes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Minutes} class.
 *
 * @since 2.18.4
 */
public class MinutesSerializer
    extends JodaSerializerBase<Minutes>
{

    private static final long serialVersionUID = 1L;

    public MinutesSerializer() {
        super(Minutes.class);
    }

    @Override
    public void serialize(Minutes value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getMinutes());
    }
}
