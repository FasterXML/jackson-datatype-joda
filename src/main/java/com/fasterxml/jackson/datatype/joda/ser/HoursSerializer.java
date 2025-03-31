package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Hours;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Hours} class.
 *
 * @since 2.18.4
 */
public class HoursSerializer
    extends JodaSerializerBase<Hours>
{

    private static final long serialVersionUID = 1L;

    public HoursSerializer() {
        super(Hours.class);
    }

    @Override
    public void serialize(Hours value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getHours());
    }
}
