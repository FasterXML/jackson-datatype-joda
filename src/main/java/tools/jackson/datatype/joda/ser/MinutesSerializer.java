package tools.jackson.datatype.joda.ser;

import org.joda.time.Minutes;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Minutes} class.
 */
public class MinutesSerializer
    extends JodaSerializerBase<Minutes>
{
    public MinutesSerializer() {
        super(Minutes.class);
    }

    @Override
    public void serialize(Minutes value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getMinutes());
    }
}
