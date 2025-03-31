package tools.jackson.datatype.joda.ser;

import org.joda.time.Seconds;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Seconds} class.
 *
 * @since 2.18.4
 */
public class SecondsSerializer
    extends JodaSerializerBase<Seconds>
{
    public SecondsSerializer() {
        super(Seconds.class);
    }

    @Override
    public void serialize(Seconds value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getSeconds());
    }
}
