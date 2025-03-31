package tools.jackson.datatype.joda.ser;

import org.joda.time.Hours;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Hours} class.
 */
public class HoursSerializer
    extends JodaSerializerBase<Hours>
{
    public HoursSerializer() {
        super(Hours.class);
    }

    @Override
    public void serialize(Hours value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getHours());
    }
}
