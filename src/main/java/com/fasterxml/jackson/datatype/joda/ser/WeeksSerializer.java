package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Weeks;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Weeks} class.
 *
 * @since 2.19
 */
public class WeeksSerializer
    extends JodaSerializerBase<Weeks>
{

    private static final long serialVersionUID = 1L;

    public WeeksSerializer() {
        super(Weeks.class);
    }

    @Override
    public void serialize(Weeks value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getWeeks());
    }
}
